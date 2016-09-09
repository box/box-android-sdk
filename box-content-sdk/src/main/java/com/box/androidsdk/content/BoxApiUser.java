package com.box.androidsdk.content;

import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.requests.BoxRequestsFile;
import com.box.androidsdk.content.requests.BoxRequestsUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents the API of the User endpoint on Box. This class can be used to generate request objects
 * for each of the APIs exposed endpoints
 */
public class BoxApiUser extends BoxApi {

    /**
     * Constructs a BoxApiUser with the provided BoxSession
     *
     * @param session authenticated session to use with the BoxApiUser
     */
    public BoxApiUser(BoxSession session) {
        super(session);
    }

    /**
     * Gets the URL for getting information of the current user
     *
     * @return the URL string for getting information of the current user
     */
    protected String getUsersUrl() { return String.format("%s/users", getBaseUri()); }

    /**
     * Gets the URL for downloading the avatar of a user
     *
     * @param id    id of the user
     * @return  the avatar download URL
     */
    protected String getAvatarDownloadUrl(String id) { return getUserInformationUrl(id) + "/avatar"; }



    /**
     * Gets the URL for getting information of the current user
     *
     * @return the URL string for getting information of the current user
     */
    protected String getUserInformationUrl(String id) { return String.format("%s/%s", getUsersUrl(), id); }


    /**
     * Gets a request that gets information about the current user
     *
     * @return request to get information about the current user
     */
        public BoxRequestsUser.GetUserInfo getCurrentUserInfoRequest() {
            BoxRequestsUser.GetUserInfo request = new BoxRequestsUser.GetUserInfo(getUserInformationUrl("me"), mSession);
        return request;
    }

    /**
     * Gets a request that gets information about a user
     *
     * @param id    id of the user to get information on
     * @return request to get information about a user
     */
    public BoxRequestsUser.GetUserInfo getUserInfoRequest(String id) {
        BoxRequestsUser.GetUserInfo request = new BoxRequestsUser.GetUserInfo(getUserInformationUrl(id), mSession);
        return request;
    }

    /**
     * Gets a request that gets all the users of an enterprise
     * The session provided must be associated with an enterprise admin user
     *
     * @return request to get all users of an enterprise
     */
    public BoxRequestsUser.GetEnterpriseUsers getEnterpriseUsersRequest() {
        BoxRequestsUser.GetEnterpriseUsers request = new BoxRequestsUser.GetEnterpriseUsers(getUsersUrl(), mSession);
        return request;
    }

    /**
     * Gets a request that creates an enterprise user
     * The session provided must be associated with an enterprise admin user
     *
     * @return request to create an enterprise user
     */
    public BoxRequestsUser.CreateEnterpriseUser getCreateEnterpriseUserRequest(String login, String name) {
        BoxRequestsUser.CreateEnterpriseUser request = new BoxRequestsUser.CreateEnterpriseUser(getUsersUrl(), mSession, login, name);
        return request;
    }

    /**
     * Gets a request that deletes an enterprise user
     * The session provided must be associated with an enterprise admin user
     *
     * @return request to delete an enterprise user
     */
    public BoxRequestsUser.DeleteEnterpriseUser getDeleteEnterpriseUserRequest(String userId) {
        BoxRequestsUser.DeleteEnterpriseUser request = new BoxRequestsUser.DeleteEnterpriseUser(getUserInformationUrl(userId), mSession, userId);
        return request;
    }

    /**
     * Gets a request that downloads an avatar of the target user id.
     *
     * @param target    target file to download thumbnail to
     * @param userId    id of user to download avatar of
     * @return  request to download a thumbnail to a target file
     * @throws IOException
     */
    public BoxRequestsFile.DownloadFile getDownloadAvatarRequest(File target, String userId) throws IOException{
        if (!target.exists()){
            throw new FileNotFoundException();
        }
        BoxRequestsFile.DownloadFile request = new BoxRequestsFile.DownloadFile(userId, target, getAvatarDownloadUrl(userId), mSession);
        return request;
    }

    /**
     * Gets a request that downloads the given avatar to the provided outputStream. Developer is responsible for closing the outputStream provided.
     *
     * @param outputStream outputStream to write file contents to.
     * @param userId the file id to download.
     * @return  request to download a file thumbnail
     */
    public BoxRequestsFile.DownloadFile getDownloadAvatarRequest(OutputStream outputStream, String userId) {
        BoxRequestsFile.DownloadFile request = new BoxRequestsFile.DownloadFile(userId, outputStream, getAvatarDownloadUrl(userId), mSession);
        return request;
    }


}
