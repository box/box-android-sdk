package com.box.androidsdk.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.box.androidsdk.content.BoxApiFile;
import com.box.androidsdk.content.BoxApiFolder;
import com.box.androidsdk.content.BoxApiMetadata;
import com.box.androidsdk.content.BoxConfig;
import com.box.androidsdk.content.BoxException;
import com.box.androidsdk.content.BoxFutureTask;
import com.box.androidsdk.content.auth.BoxAuthentication;
import com.box.androidsdk.content.models.BoxEntity;
import com.box.androidsdk.content.models.BoxError;
import com.box.androidsdk.content.models.BoxFile;
import com.box.androidsdk.content.models.BoxFolder;
import com.box.androidsdk.content.models.BoxItem;
import com.box.androidsdk.content.models.BoxList;
import com.box.androidsdk.content.models.BoxListItems;
import com.box.androidsdk.content.models.BoxMetadata;
import com.box.androidsdk.content.models.BoxMetadataUpdateTask;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;
import com.box.androidsdk.content.requests.BoxRequestsMetadata;
import com.box.androidsdk.content.requests.BoxResponse;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class MainActivity extends ActionBarActivity implements BoxAuthentication.AuthListener {

    BoxSession mSession = null;
    BoxSession mOldSession = null;

    private ListView mListView;

    private ProgressDialog mDialog;

    private ArrayAdapter<BoxItem> mAdapter;

    private BoxApiFolder mFolderApi;
    private BoxApiFile mFileApi;
    private BoxApiMetadata mMetadataApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new BoxItemAdapter(this);
        mListView.setAdapter(mAdapter);
        BoxConfig.IS_LOG_ENABLED = true;
        BoxConfig.CLIENT_ID = "m9bcgls0kffgyclmt9hz0jbs9ua7m0yy";
        BoxConfig.CLIENT_SECRET = "mojjTtvkNvh4T2B2mNYkOtfkd0uo6zod";
        // needs to match redirect uri in developer settings if set.
//        BoxConfig.REDIRECT_URL = "<YOUR_REDIRECT_URI>";
        initialize();
    }

    private void initialize() {
        mAdapter.clear();

        mSession = new BoxSession(this, null);
        mSession.setSessionAuthListener(this);
        mSession.authenticate();
    }

    private void loadRootFolder() {
        new Thread() {
            @Override
            public void run() {
                try {
                    final BoxListItems folderItems = mFolderApi.getItemsRequest("0").send();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addAll(folderItems);
                        }
                    });
                } catch (BoxException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void uploadFile() {
        mDialog = ProgressDialog.show(MainActivity.this, getText(R.string.boxsdk_Please_wait), getText(R.string.boxsdk_Please_wait));
        new Thread() {
            @Override
            public void run() {
                try {
                    String uploadFileName = "box_logo.png";
                    InputStream uploadStream = getResources().getAssets().open(uploadFileName);
                    String destinationFolderId = "0";
                    String uploadName = "BoxSDKUpload.png";
                    BoxRequestsFile.UploadFile request = mFileApi.getUploadRequest(uploadStream, uploadName, destinationFolderId);
                    final BoxFile uploadFileInfo = request.send();
                    showToast("Uploaded " + uploadFileInfo.getName());
                    loadRootFolder();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BoxException e) {
                    e.printStackTrace();
                    BoxError error = e.getAsBoxError();
                    if (error != null && error.getStatus() == HttpStatus.SC_CONFLICT) {
                        ArrayList<BoxEntity> conflicts = error.getContextInfo().getConflicts();
                        if (conflicts != null && conflicts.size() == 1 && conflicts.get(0) instanceof BoxFile) {
                            uploadNewVersion((BoxFile) conflicts.get(0));
                            return;
                        }
                    }
                    showToast("Upload failed");
                } finally {
                    mDialog.dismiss();
                }
            }
        }.start();

    }

    private void uploadNewVersion(final BoxFile file) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String uploadFileName = "box_logo.png";
                    InputStream uploadStream = getResources().getAssets().open(uploadFileName);
                    BoxRequestsFile.UploadNewVersion request = mFileApi.getUploadNewVersionRequest(uploadStream, file.getId());
                    final BoxFile uploadFileVersionInfo = request.send();
                    showToast("Uploaded new version of " + uploadFileVersionInfo.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BoxException e) {
                    e.printStackTrace();
                    showToast("Upload failed");
                } finally {
                    mDialog.dismiss();
                }
            }
        }.start();
    }

    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void clearAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.clear();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int numAccounts = BoxAuthentication.getInstance().getStoredAuthInfo(this).keySet().size();
        menu.findItem(R.id.logoutAll).setVisible(numAccounts > 1);
        menu.findItem(R.id.logout).setVisible(numAccounts > 0);
        menu.findItem(R.id.switch_accounts).setTitle(numAccounts > 0 ? R.string.switch_accounts : R.string.login);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload) {
            uploadFile();
            return true;
        } else if (id == R.id.switch_accounts) {
            switchAccounts();
            return true;
        } else if (id == R.id.logout) {
            mSession.logout();
            initialize();
            return true;
        } else if (id == R.id.logoutAll) {
            new Thread() {
                @Override
                public void run() {
                    BoxAuthentication.getInstance().logoutAllUsers(getApplicationContext());
                }
            }.start();
            return true;
        } else if (id == R.id.metadata) {
            getMetadata();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getMetadata() {
        mDialog = ProgressDialog.show(MainActivity.this, getText(R.string.boxsdk_Please_wait), getText(R.string.boxsdk_Please_wait));
        new Thread() {
            @Override
            public void run() {
                try {
                    String id = "33710964929";

                    // Get All Metadata
//                    BoxRequestsMetadata.GetFileMetadata request = mMetadataApi.getGetMetadataRequest(id);
                    mMetadataApi.getGetMetadataRequest(id).send();

                    // Get Template Metadata
//                    BoxRequestsMetadata.GetFileMetadata request = mMetadataApi.getGetMetadataRequest(id, "salesContract");
                    mMetadataApi.getGetMetadataRequest(id, "salesContract").send();

                    // Get All Available Metadata Templates
//                    BoxRequestsMetadata.GetMetadataTemplates request = mMetadataApi.getGetMetadataTemplatesRequest();
                    mMetadataApi.getGetMetadataTemplatesRequest().send();

                    // Get Schema For Specific Metadata Template
//                    BoxRequestsMetadata.GetMetadataTemplates request = mMetadataApi.getGetMetadataTemplateSchemaRequest("salesContract");
                    mMetadataApi.getGetMetadataTemplateSchemaRequest("salesContract").send();

                    // Delete Template
                    try {
                        mMetadataApi.getDeleteTemplateMetadataRequest(id, "invoice").send();
                    } catch (BoxException e) {
                        // do nothing, metadata template just doesn't exist yet for the file
                    }

                    // Make sure comment add still works
//                    mFileApi.getAddCommentRequest(id, "Test comment 2222").send();

                    // Create Template
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("invoiceNumber", "12345");
                    map.put("companyName", "Boxers");
                    map.put("terms", "30");
//                    BoxRequestsMetadata.AddMetadataToFile request = mMetadataApi.getAddMetadataRequest(id, map, "enterprise", "invoice");
                    mMetadataApi.getAddMetadataRequest(id, map, "enterprise", "invoice").send();

                    // Update Template
                    BoxList<BoxMetadataUpdateTask> updateTaskList = new BoxList<BoxMetadataUpdateTask>();
                    updateTaskList.add(new BoxMetadataUpdateTask(BoxMetadataUpdateTask.BoxMetadataUpdateOperations.BoxMetadataUpdateTEST, "companyName", "Boxers"));
                    updateTaskList.add(new BoxMetadataUpdateTask(BoxMetadataUpdateTask.BoxMetadataUpdateOperations.BoxMetadataUpdateREMOVE, "companyName"));
                    updateTaskList.add(new BoxMetadataUpdateTask(BoxMetadataUpdateTask.BoxMetadataUpdateOperations.BoxMetadataUpdateTEST, "terms", "30"));
                    updateTaskList.add(new BoxMetadataUpdateTask(BoxMetadataUpdateTask.BoxMetadataUpdateOperations.BoxMetadataUpdateREPLACE, "terms", "60"));
                    updateTaskList.add(new BoxMetadataUpdateTask(BoxMetadataUpdateTask.BoxMetadataUpdateOperations.BoxMetadataUpdateADD, "approved", "Yes"));
                    BoxRequestsMetadata.UpdateFileMetadata request = mMetadataApi.getUpdateMetadataRequest(id, updateTaskList, "enterprise", "invoice");

                    final BoxMetadata metadata = request.send();
                    showToast("Metadata Request Successful!");
                    Intent intent = new Intent(MainActivity.this, ShowMetadataActivity.class);
                    intent.putExtra("metadata", metadata.toJson());
                    startActivity(intent);
                } catch (BoxException e) {
                    e.printStackTrace();
                    System.out.println(e.getResponse());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mDialog.dismiss();
                }
            }
        }.start();
    }

    private void switchAccounts() {
        mOldSession = mSession;
        mSession = new BoxSession(this, null);
        mSession.setSessionAuthListener(this);
        mSession.authenticate().addOnCompletedListener(new BoxFutureTask.OnCompletedListener<BoxSession>() {
            @Override
            public void onCompleted(BoxResponse<BoxSession> response) {
                if (response.isSuccess()) {
                    clearAdapter();
                    onAuthCreated(mSession.getAuthInfo());
                }
            }
        });
    }

    @Override
    public void onRefreshed(BoxAuthentication.BoxAuthenticationInfo info) {
        // do nothing
    }

    @Override
    public void onAuthCreated(BoxAuthentication.BoxAuthenticationInfo info) {
        mFolderApi = new BoxApiFolder(mSession);
        mFileApi = new BoxApiFile(mSession);
        mMetadataApi = new BoxApiMetadata(mSession);

        loadRootFolder();
    }

    @Override
    public void onAuthFailure(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        if (ex != null) {
            clearAdapter();
        } else if (info == null && mOldSession != null) {
            mSession = mOldSession;
            mOldSession = null;
            onAuthCreated(mSession.getAuthInfo());
        }
    }

    @Override
    public void onLoggedOut(BoxAuthentication.BoxAuthenticationInfo info, Exception ex) {
        clearAdapter();
    }

    private class BoxItemAdapter extends ArrayAdapter<BoxItem> {
        public BoxItemAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BoxItem item = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.boxsdk_list_item, parent, false);
            }

            TextView name = (TextView) convertView.findViewById(R.id.name);
            name.setText(item.getName());

            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            if (item instanceof BoxFolder) {
                icon.setImageResource(R.drawable.boxsdk_icon_folder_yellow_private);
            } else {
                icon.setImageResource(R.drawable.boxsdk_generic);
            }

            return convertView;
        }

    }
}
