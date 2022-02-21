package app.manager.dating.constants;

public interface Constants {

    // Attention! You can only change values of following constants:

    // EMOJI_KEYBOARD, WEB_SITE_AVAILABLE, GOOGLE_PAY_TEST_BUTTON, FACEBOOK_AUTHORIZATION,
    // APP_TEMP_FOLDER, VIDEO_FILE_MAX_SIZE, BILLING_KEY, CLIENT_ID, API_DOMAIN, WEB_SITE,
    // GHOST_MODE_COST, VERIFIED_BADGE_COST, DISABLE_ADS_COST,
    // PRO_MODE_COST, HASHTAGS_COLOR

    // It is forbidden to change value of constants, which are not indicated above !!!

    int VOLLEY_REQUEST_SECONDS = 15; //SECONDS TO REQUEST

    String WEB_SITE = "https://datingnet.me/";  //web site url address

    String APP_TEMP_FOLDER = "chat"; //directory for temporary storage of images from the camera

    // Client ID For identify the application | Must be the same with CLIENT_ID from server config: db.inc.php

    String CLIENT_ID = "1";                         // old 11; Correct example: 12567 | Incorrect example: 0987

    // Client Secret | Text constant | Must be the same with CLIENT_SECRET from server config: db.inc.php

    String CLIENT_SECRET = "wFt4*KBoNN_kjSdG13m1k3k=";    // Example: "wFt4*KBoNN_kjSdG13m1k3k="

    String API_DOMAIN = "https://datingnet.me/";  // url address to which the application sends requests | with back slash "/" at the end | example: https://mysite.com/ | for emulator on localhost: http://10.0.2.2/

    String API_FILE_EXTENSION = "";                 // Don`t change value for this constant!
    String API_VERSION = "v2";                      // Don`t change value for this constant!
    String API_MANAGER_DIR = "manager";             // Don`t change value for this constant!

    String METHOD_MANAGER_SIGNIN = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.signIn" + API_FILE_EXTENSION;
    String METHOD_MANAGER_LOGOUT = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.logOut" + API_FILE_EXTENSION;
    String METHOD_MANAGER_AUTHORIZE = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.authorize" + API_FILE_EXTENSION;
    String METHOD_MANAGER_SET_FCM_TOKEN = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.setFcmToken" + API_FILE_EXTENSION;

    String METHOD_MANAGER_GET_SETTINGS = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.getSettings" + API_FILE_EXTENSION;

    String METHOD_MANAGER_GET_NOT_MODERATED_PROFILE_PHOTOS = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.getNotModeratedProfilePhotos" + API_FILE_EXTENSION;
    String METHOD_MANAGER_GET_NOT_MODERATED_PROFILE_COVERS = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.getNotModeratedProfileCovers" + API_FILE_EXTENSION;
    String METHOD_MANAGER_GET_NOT_MODERATED_MEDIA_ITEMS = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.getNotModeratedMediaItems" + API_FILE_EXTENSION;

    String METHOD_MANAGER_APPROVE_PROFILE_PHOTO = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.approveProfilePhoto" + API_FILE_EXTENSION;
    String METHOD_MANAGER_REJECT_PROFILE_PHOTO = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.rejectProfilePhoto" + API_FILE_EXTENSION;

    String METHOD_MANAGER_APPROVE_PROFILE_COVER = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.approveProfileCover" + API_FILE_EXTENSION;
    String METHOD_MANAGER_REJECT_PROFILE_COVER = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.rejectProfileCover" + API_FILE_EXTENSION;

    String METHOD_MANAGER_APPROVE_GALLERY_ITEM = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.approveGalleryItem" + API_FILE_EXTENSION;
    String METHOD_MANAGER_REJECT_GALLERY_ITEM = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.rejectGalleryItem" + API_FILE_EXTENSION;

    String METHOD_MANAGER_PROFILE_GET = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.profileGet" + API_FILE_EXTENSION;

    String METHOD_MANAGER_PROFILE_BLOCK = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.profileBlock" + API_FILE_EXTENSION;

    String METHOD_MANAGER_PASSWORD_SET = API_DOMAIN + "api/" + API_VERSION + "/method/" + API_MANAGER_DIR + "/app.setPassword" + API_FILE_EXTENSION;

    String METHOD_APP_TERMS = API_DOMAIN + "api/" + API_VERSION + "/method/app.terms" + API_FILE_EXTENSION;
    String METHOD_APP_THANKS = API_DOMAIN + "api/" + API_VERSION + "/method/app.thanks" + API_FILE_EXTENSION;
    String METHOD_APP_SEARCH = API_DOMAIN + "api/" + API_VERSION + "/method/app.search" + API_FILE_EXTENSION;

    String METHOD_APP_SEARCH_PRELOAD = API_DOMAIN + "api/" + API_VERSION + "/method/app.searchPreload" + API_FILE_EXTENSION;

    String TAG_UPDATE_BADGES = "update_badges";

    int ITEM_ACTION_REJECT = 0;
    int ITEM_ACTION_APPROVE = 1;
    int ITEM_ACTION_MENU = 2;

    int APP_TYPE_ALL = -1;
    int APP_TYPE_MANAGER = 0;
    int APP_TYPE_WEB = 1;
    int APP_TYPE_ANDROID = 2;
    int APP_TYPE_IOS = 3;

    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_PHOTO = 1;                  //WRITE_EXTERNAL_STORAGE
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_COVER = 2;                  //WRITE_EXTERNAL_STORAGE
    int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 3;                               //ACCESS_COARSE_LOCATION
    int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4;                        //WRITE_EXTERNAL_STORAGE

    int APP_BAR_WITH_ADMOB_HEIGHT = 127;
    int APP_BAR_WITHOUT_ADMOB_HEIGHT = 50;

    int LIST_ITEMS = 20;
    int HOTGAME_LIST_ITEMS = 500;

    int ENABLED = 1;
    int DISABLED = 0;

    int GCM_ENABLED = 1;
    int GCM_DISABLED = 0;

    int ADMOB_ENABLED = 1;
    int ADMOB_DISABLED = 0;

    int COMMENTS_ENABLED = 1;
    int COMMENTS_DISABLED = 0;

    int MESSAGES_ENABLED = 1;
    int MESSAGES_DISABLED = 0;

    int ERROR_SUCCESS = 0;

    int SEX_MALE = 0;
    int SEX_FEMALE = 1;
    int SEX_UNKNOWN = 2;

    int NOTIFY_TYPE_LIKE = 0;
    int NOTIFY_TYPE_FOLLOWER = 1;
    int NOTIFY_TYPE_MESSAGE = 2;
    int NOTIFY_TYPE_COMMENT = 3;
    int NOTIFY_TYPE_COMMENT_REPLY = 4;
    int NOTIFY_TYPE_FRIEND_REQUEST_ACCEPTED = 5;
    int NOTIFY_TYPE_GIFT = 6;

    int NOTIFY_TYPE_IMAGE_COMMENT = 7;
    int NOTIFY_TYPE_IMAGE_COMMENT_REPLY = 8;
    int NOTIFY_TYPE_IMAGE_LIKE = 9;

    int NOTIFY_TYPE_MEDIA_APPROVE = 10;
    int NOTIFY_TYPE_MEDIA_REJECT = 11;

    int NOTIFY_TYPE_PROFILE_PHOTO_APPROVE = 12;
    int NOTIFY_TYPE_PROFILE_PHOTO_REJECT = 13;

    int NOTIFY_TYPE_ACCOUNT_APPROVE = 14;
    int NOTIFY_TYPE_ACCOUNT_REJECT = 15;

    int NOTIFY_TYPE_PROFILE_COVER_APPROVE = 16;
    int NOTIFY_TYPE_PROFILE_COVER_REJECT = 17;

    int GCM_NOTIFY_CONFIG = 0;
    int GCM_NOTIFY_SYSTEM = 1;
    int GCM_NOTIFY_CUSTOM = 2;
    int GCM_NOTIFY_LIKE = 3;
    int GCM_NOTIFY_ANSWER = 4;
    int GCM_NOTIFY_QUESTION = 5;
    int GCM_NOTIFY_COMMENT = 6;
    int GCM_NOTIFY_FOLLOWER = 7;
    int GCM_NOTIFY_PERSONAL = 8;
    int GCM_NOTIFY_MESSAGE = 9;
    int GCM_NOTIFY_COMMENT_REPLY = 10;
    int GCM_FRIEND_REQUEST_INBOX = 11;
    int GCM_FRIEND_REQUEST_ACCEPTED = 12;
    int GCM_NOTIFY_GIFT = 14;
    int GCM_NOTIFY_SEEN = 15;
    int GCM_NOTIFY_TYPING = 16;
    int GCM_NOTIFY_URL = 17;

    int GCM_NOTIFY_IMAGE_COMMENT_REPLY = 18;
    int GCM_NOTIFY_IMAGE_COMMENT = 19;
    int GCM_NOTIFY_IMAGE_LIKE = 20;

    int GCM_NOTIFY_TYPING_START = 27;
    int GCM_NOTIFY_TYPING_END = 28;

    int GCM_NOTIFY_REFERRAL = 24;
    int GCM_NOTIFY_MATCH = 25;

    int GCM_NOTIFY_MEDIA_APPROVE = 1001;
    int GCM_NOTIFY_MEDIA_REJECT = 1002;

    int GCM_NOTIFY_PROFILE_PHOTO_APPROVE = 1003;
    int GCM_NOTIFY_PROFILE_PHOTO_REJECT = 1004;

    int GCM_NOTIFY_ACCOUNT_APPROVE = 1005;
    int GCM_NOTIFY_ACCOUNT_REJECT = 1006;

    int GCM_NOTIFY_PROFILE_COVER_APPROVE = 1007;
    int GCM_NOTIFY_PROFILE_COVER_REJECT = 1008;

    int GCM_NOTIFY_CHANGE_ACCOUNT_SETTINGS = 30;

    int GCM_NOTIFY_PROFILE_NEW_PROFILE_PHOTO_UPLOADED = 2001;
    int GCM_NOTIFY_PROFILE_NEW_PROFILE_COVER_UPLOADED = 2002;
    int GCM_NOTIFY_PROFILE_NEW_MEDIA_ITEM_UPLOADED = 2003;

    int ERROR_LOGIN_TAKEN = 300;
    int ERROR_EMAIL_TAKEN = 301;
    int ERROR_FACEBOOK_ID_TAKEN = 302;
    int ERROR_MULTI_ACCOUNT = 500;

    int ERROR_OTP_VERIFICATION = 506;
    int ERROR_OTP_PHONE_NUMBER_TAKEN = 507;

    int ACCOUNT_STATE_ENABLED = 0;
    int ACCOUNT_STATE_DISABLED = 1;
    int ACCOUNT_STATE_BLOCKED = 2;
    int ACCOUNT_STATE_DEACTIVATED = 3;

    int ACCOUNT_TYPE_USER = 0;
    int ACCOUNT_TYPE_GROUP = 1;

    int GALLERY_ITEM_TYPE_IMAGE = 0;
    int GALLERY_ITEM_TYPE_VIDEO = 1;

    int ERROR_UNKNOWN = 100;
    int ERROR_ACCESS_TOKEN = 101;

    int ERROR_ACCOUNT_ID = 400;

    int ERROR_CLIENT_ID = 19100;
    int ERROR_CLIENT_SECRET = 19101;

    int UPLOAD_TYPE_PHOTO = 0;
    int UPLOAD_TYPE_COVER = 1;

    int ACTION_NEW = 1;
    int ACTION_EDIT = 2;
    int SELECT_POST_IMG = 3;
    int VIEW_CHAT = 4;
    int CREATE_POST_IMG = 5;
    int SELECT_CHAT_IMG = 6;
    int CREATE_CHAT_IMG = 7;
    int FEED_NEW_POST = 8;
    int FRIENDS_SEARCH = 9;
    int ITEM_EDIT = 10;
    int STREAM_NEW_POST = 11;

    int SELECT_PHOTO_IMG = 20;
    int CREATE_PHOTO_IMG = 21;

    int PAGE_PROFILE = 1;
    int PAGE_GALLERY = 2;
    int PAGE_FRIENDS = 3;
    int PAGE_MATCHES = 4;
    int PAGE_MESSAGES = 5;
    int PAGE_NOTIFICATIONS = 6;
    int PAGE_GUESTS = 7;
    int PAGE_LIKES = 8;
    int PAGE_LIKED = 9;
    int PAGE_UPGRADES = 10;
    int PAGE_NEARBY = 11;
    int PAGE_MEDIA_STREAM = 12;
    int PAGE_MEDIA_FEED = 13;
    int PAGE_SEARCH = 14;
    int PAGE_SETTINGS = 15;
    int PAGE_HOTGAME = 16;
    int PAGE_FINDER = 17;
    int PAGE_MENU = 18;

    int PAGE_PROFILE_PHOTOS_MODERATION = 100;
    int PAGE_PROFILE_COVERS_MODERATION = 101;
    int PAGE_MEDIA_ITEMS_MODERATION = 102;

    //

    public static final int PA_BUY_CREDITS = 0;
    public static final int PA_BUY_GIFT = 1;
    public static final int PA_BUY_VERIFIED_BADGE = 2;
    public static final int PA_BUY_GHOST_MODE = 3;
    public static final int PA_BUY_DISABLE_ADS = 4;
    public static final int PA_BUY_REGISTRATION_BONUS = 5;
    public static final int PA_BUY_REFERRAL_BONUS = 6;
    public static final int PA_BUY_MANUAL_BONUS = 7;
    public static final int PA_BUY_PRO_MODE = 8;
    public static final int PA_BUY_SPOTLIGHT = 9;
    public static final int PA_BUY_MESSAGE_PACKAGE = 10;
    public static final int PA_SEND_TRANSFER = 11;
    public static final int PA_RECEIVE_TRANSFER = 12;

    public static final int PT_UNKNOWN = 0;
    public static final int PT_CREDITS = 1;
    public static final int PT_CARD = 2;
    public static final int PT_GOOGLE_PURCHASE = 3;
    public static final int PT_APPLE_PURCHASE = 4;
    public static final int PT_ADMOB_REWARDED_ADS = 5;
    public static final int PT_BONUS = 6;

    public static final int ADMIN_ACCESS_LEVEL_ALL_RIGHTS = 0;
    public static final int ADMIN_ACCESS_LEVEL_READ_WRITE_RIGHTS = 1;
    public static final int ADMIN_ACCESS_LEVEL_MODERATOR_RIGHTS = 2;
    public static final int ADMIN_ACCESS_LEVEL_READ_ONLY_RIGHTS = 3;

    String TAG = "TAG";

    String HASHTAGS_COLOR = "#5BCFF2";
}