package com.box.androidsdk.content.listeners;

import com.box.androidsdk.content.models.BoxDownload;

/**
 * Listener that provides information on a download once the download starts.
 */
public interface DownloadStartListener  {

    /**
     * Callback when download has started
     * @param downloadInfo
     */
    public void onStart(BoxDownload downloadInfo);

}
