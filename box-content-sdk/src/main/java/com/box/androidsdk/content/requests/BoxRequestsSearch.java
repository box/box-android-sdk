package com.box.androidsdk.content.requests;

import com.box.androidsdk.content.utils.BoxDateFormat;
import com.box.androidsdk.content.models.BoxSession;
import com.box.androidsdk.content.models.BoxList;
import com.box.androidsdk.content.utils.SdkUtils;

import java.util.Date;
import java.util.Locale;

/**
 * Search requests.
 */
public class BoxRequestsSearch {

    /**
     * Request for searching.
     */
    public static class Search extends BoxRequest<BoxList, Search> {

        /**
         * Only search in names.
         */
        public static String CONTENT_TYPE_NAME = "name";
        /**
         * Only search in descriptions.
         */
        public static String CONTENT_TYPE_DESCRIPTION = "description";
        /**
         * Only search in comments.
         */
        public static String CONTENT_TYPE_COMMENTS = "comments";
        /**
         * Only search in file contents.
         */
        public static String CONTENT_TYPE_FILE_CONTENTS = "file_content";
        /**
         * Only search in tags.
         */
        public static String CONTENT_TYPE_TAGS = "tags";

        public static enum Scope {
            USER_CONTENT,
            /**
             * This scope only works for administrator, and may need special permission. Please check
             * https://developers.box.com/docs/#search for details.
             */
            ENTERPRISE_CONTENT
        }

        /**
         * Creates a search request with the default parameters.
         *
         * @param query query to search for.
         * @param requestUrl    URL of the search endpoint.
         * @param session   the authenticated session that will be used to make the request with
         */
        public Search(String query, String requestUrl, BoxSession session) {
            super(BoxList.class, requestUrl, session);
            limitValueForKey("query", query);
            mRequestMethod = Methods.GET;
        }

        /**
         * limit key to certain value.
         */
        public Search limitValueForKey(String key, String value) {
            mQueryMap.put(key, value);
            return this;
        }

        /**
         * limit search scope. Please check
         * https://developers.box.com/docs/#search for details.
         */
        public Search limitSearchScope(Scope scope) {
            limitValueForKey("scope", scope.name().toLowerCase(Locale.US));
            return this;
        }

        /**
         * limit file search to given file extensions.
         */
        public Search limitFileExtensions(String[] extensions) {
            limitValueForKey("file_extensions", SdkUtils.concatStringWithDelimiter(extensions, ","));
            return this;
        }

        /**
         * Limit the search to creation time between fromDate to toDate.
         *
         * @param fromDate can use null if you don't want to restrict this.
         * @param toDate   can use null if you don't want to restrict this.
         */
        public Search limitCreationTime(Date fromDate, Date toDate) {
            addTimeRange("created_at_range", fromDate, toDate);
            return this;
        }

        /**
         * Limit the search to last update time between fromDate to toDate.
         *
         * @param fromDate can use null if you don't want to restrict this.
         * @param toDate   can use null if you don't want to restrict this.
         */
        public Search limitLastUpdateTime(Date fromDate, Date toDate) {
            addTimeRange("updated_at_range", fromDate, toDate);
            return this;
        }

        /**
         * Limit the search to file size greater than minSize in bytes and less than maxSize in bytes.
         */
        public Search limitSizeRange(long minSize, long maxSize) {
            limitValueForKey("size_range", String.format("%d,%d", minSize, maxSize));
            return this;
        }

        /**
         * limit the search to items owned by given users.
         */
        public Search limitOwnerUserIds(String[] userIds) {
            limitValueForKey("owner_user_ids", SdkUtils.concatStringWithDelimiter(userIds, ","));
            return this;
        }

        /**
         * Limit searches to specific ancestor folders.
         */
        public Search limitAncestorFolderIds(String[] folderIds) {
            limitValueForKey("ancestor_folder_ids", SdkUtils.concatStringWithDelimiter(folderIds, ","));
            return this;
        }

        /**
         * Limit search to certain content types. The allowed content type strings are defined as final static in this class.
         * e.g. Search.CONTENT_TYPE_NAME, Search.CONTENT_TYPE_DESCRIPTION...
         */
        public Search limitContentTypes(String[] contentTypes) {
            limitValueForKey("content_types", SdkUtils.concatStringWithDelimiter(contentTypes, ","));
            return this;
        }

        /**
         * The type you want to return in your search. Can be BoxFile.TYPE, BoxFolder.TYPE...
         */
        public Search limitType(String type) {
            limitValueForKey("type", type);
            return this;
        }

        /**
         * Sets the limit of items that should be returned
         *
         * @param limit limit of items to return
         * @return the get folder items request
         */
        public Search setLimit(int limit) {
            limitValueForKey("limit", String.valueOf(limit));
            return this;
        }

        /**
         * Sets the offset of the items that should be returned
         *
         * @param offset offset of items to return
         * @return the offset of the items to return
         */
        public Search setOffset(int offset) {
            limitValueForKey("offset", String.valueOf(offset));
            return this;
        }

        private void addTimeRange(String key, Date fromDate, Date toDate) {
            String range = BoxDateFormat.getTimeRangeString(fromDate, toDate);
            if (!SdkUtils.isEmptyString(range)) {
                limitValueForKey(key, range);
            }
        }
    }

}
