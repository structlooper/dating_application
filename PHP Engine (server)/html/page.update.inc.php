<?php

    /*!
     * https://raccoonsquare.com
     * raccoonsquare@gmail.com
     *
     * Copyright 2012-2021 Demyanchuk Dmitry (raccoonsquare@gmail.com)
     */

    error_reporting(E_ALL);

    if (!defined("APP_SIGNATURE")) {

        header("Location: /");
        exit;
    }

    require_once 'html/recaptcha/autoload.php';

    //

    $update = new update($dbo);
    $update->modifyColumnSettingsTable1();
    unset($update);

    //

    $error = false;
    $pcode = '';
    $raccoonsquare_response = "";

    $settings = new settings($dbo);

    $settings_array = $settings->get();

    if (!empty($_POST)) {

        $pcode = isset($_POST['pcode']) ? $_POST['pcode'] : '';
        $token = isset($_POST['authenticity_token']) ? $_POST['authenticity_token'] : '';
        $recaptcha_token = isset($_POST['recaptcha_token']) ? $_POST['recaptcha_token'] : '';

        $pcode = helper::clearText($pcode);
        $pcode = helper::escapeText($pcode);

        // Google Recaptcha

        $recaptcha = new \ReCaptcha\ReCaptcha(RECAPTCHA_SECRET_KEY);
        $resp = $recaptcha->verify($recaptcha_token, $_SERVER['REMOTE_ADDR']);

        if (!$resp->isSuccess()){

            $error = true;
            $error_message[] = "Google Recaptcha error";
        }

        if (auth::getAuthenticityToken() !== $token) {

            $error = true;
            $error_message[] = $LANG['msg-error-unknown'];
        }

        if (!preg_match("/^([a-f0-9]{8})-(([a-f0-9]{4})-){3}([a-f0-9]{12})$/i", $pcode)) {

            $error = true;
            $error_message[] = 'Invalid purchase code';
        }

        if (!$error) {

            $raccoonsquare_response = helper::verify_pcode($pcode, ENVATO_ITEM_ID);

            $raccoonsquare_response = json_decode(json_encode($raccoonsquare_response, JSON_FORCE_OBJECT));

            if (is_object($raccoonsquare_response)) {

                if (!$raccoonsquare_response->error) {

                    if ($raccoonsquare_response->error_code == ENVATO_ERROR_PCODE_REGISTERED) {

                        $settings = new settings($dbo);
                        $settings->createValue("envato_pcode", 0, $pcode); //Save purchase code
                        unset($settings);
                    }
                }
            }
        }

    } else {

        if (array_key_exists('envato_pcode', $settings_array)) {

            $raccoonsquare_response = helper::verify_pcode($settings_array['envato_pcode']['textValue'], ENVATO_ITEM_ID);

            $raccoonsquare_response = json_decode(json_encode($raccoonsquare_response, JSON_FORCE_OBJECT));
        }
    }

    if (is_object($raccoonsquare_response)) {

        if ($raccoonsquare_response->error) {

            $error = true;
            $error_message[] = $LANG['Invalid purchase code'];

        } else {

            switch ($raccoonsquare_response->error_code) {

                case ENVATO_ERROR_PCODE_INVALID: {

                    if (array_key_exists('envato_pcode', $settings_array)) {

                        $settings->deleteValue('envato_pcode');
                    }

                    $error = true;
                    $error_message[] = 'Invalid purchase code';

                    break;
                }

                case ENVATO_ERROR_PCODE_UNKNOWN: {

                    if (array_key_exists('envato_pcode', $settings_array)) {

                        $settings->deleteValue('envato_pcode');
                    }

                    $error = true;
                    $error_message[] = 'We were unable to verify this purchase code. Try later..';

                    break;
                }

                case ENVATO_ERROR_PCODE_ILLEGAL: {

                    echo "";

                    if (array_key_exists('envato_pcode', $settings_array)) {

                        $settings->deleteValue('envato_pcode');
                    }

                    $error = true;
                    $error_message[] = 'This purchase code is already in use for another domain.';

                    break;
                }

                default: {

                    $error = false;

                    include_once("sys/core/initialize.inc.php");

                    $update = new update($dbo);
                    $update->addColumnToChatsTable();
                    $update->addColumnToChatsTable2();

                    $update->addColumnToAdminsTable();

                    $update->addColumnToUsersTable15();

                    $update->addColumnToGalleryTable1();
                    $update->addColumnToGalleryTable2();
                    $update->addColumnToGalleryTable3();

                    $update->addColumnToUsersTable1();
                    $update->addColumnToUsersTable2();
                    $update->addColumnToUsersTable3();
                    $update->addColumnToUsersTable4();
                    $update->addColumnToUsersTable5();

                    // For version 2.7

                    $update->addColumnToUsersTable6();

                    // Only For version 2.8

                    $update->updateUsersTable();

                    // For version 3.0

                    $update->addColumnToUsersTable7();
                    $update->addColumnToUsersTable8();
                    $update->addColumnToUsersTable9();
                    $update->addColumnToUsersTable10();

                    // For version 3.1

                    $update->addColumnToUsersTable11();
                    $update->addColumnToUsersTable12();

                    // For version 3.2

                    $update->addColumnToUsersTable14();

                    // For version 3.4

                    $update->addColumnToMessagesTable1();

                    // For version 3.5

                    $update->addColumnToUsersTable16(); // add field sex_orientation
                    $update->addColumnToUsersTable17(); // add field u_age
                    $update->addColumnToUsersTable18(); // add field u_height
                    $update->addColumnToUsersTable19(); // add field u_weight

                    $update->addColumnToUsersTable20();
                    $update->addColumnToUsersTable21();
                    $update->addColumnToUsersTable22();

                    // For version 3.6

                    $update->addColumnToUsersTable23();
                    $update->addColumnToUsersTable24();
                    $update->addColumnToUsersTable25();

                    $settings = new settings($dbo);
                    $settings->createValue("admob", 1); //Default show admob
                    $settings->createValue("defaultBalance", 10); //Default balance for new users
                    $settings->createValue("defaultReferralBonus", 10); //Default bonus - referral signup
                    $settings->createValue("defaultFreeMessagesCount", 150); //Default free messages count after signup
                    $settings->createValue("allowFriendsFunction", 1);
                    $settings->createValue("allowSeenTyping", 1);
                    $settings->createValue("allowMultiAccountsFunction", 1);
                    $settings->createValue("allowFacebookAuthorization", 1);
                    $settings->createValue("allowUpgradesSection", 1);
                    unset($settings);

                    // For version 3.7

                    $settings = new settings($dbo);
                    $settings->createValue("allowSeenTyping", 1);
                    unset($settings);

                    $update->addColumnToUsersTable26();

                    // For version 3.8

                    $settings = new settings($dbo);
                    $settings->createValue("allowRewardedAds", 1); //Default allow rewarded ads
                    unset($settings);

                    // For version 4.1

                    $update->addColumnToGalleryTable4();

                    // For version 4.2

                    $update->addColumnToGalleryTable5();

                    // For version 4.3

                    $update->addColumnToUsersTable27();
                    $update->addColumnToUsersTable28();
                    $update->addColumnToUsersTable29();
                    $update->addColumnToUsersTable30();
                    $update->addColumnToUsersTable31();

                    // For version 4.5

                    $update->addColumnToUsersTable32();
                    $update->addColumnToUsersTable33();
                    $update->addColumnToUsersTable34();
                    $update->addColumnToUsersTable35();
                    $update->addColumnToUsersTable36();
                    $update->addColumnToUsersTable37();
                    $update->addColumnToUsersTable38();

                    // For version 4.6

                    $update->addColumnToAccessDataTable1();
                    $update->addColumnToAccessDataTable2();
                    $update->addColumnToAccessDataTable3();

                    $update->addColumnToUsersTable39();

                    $settings = new settings($dbo);
                    $settings->createValue("photoModeration", 1); //Default on
                    $settings->createValue("coverModeration", 1); //Default on
                    $settings->createValue("galleryModeration", 1); //Default on
                    $settings->createValue("allowAdBannerInGalleryItem", 1); //Default on
                    $settings->createValue("defaultGhostModeCost", 100); //Default cost for ghost mode in credits
                    $settings->createValue("defaultVerifiedBadgeCost", 150); //Default cost for verified badge in credits
                    $settings->createValue("defaultDisableAdsCost", 200); //Default cost for disable ads in credits
                    $settings->createValue("defaultProModeCost", 170); //Default cost for pro mode feature in credits
                    $settings->createValue("defaultSpotlightCost", 30); //Default cost for adding to spotlight feature in credits
                    $settings->createValue("defaultMessagesPackageCost", 20); //Default cost for buy message package feature in credits
                    unset($settings);

                    // For version 5.0

                    // $update->updateUsersTable1();

                    //    $settings = new settings($dbo);
                    //    $settings->createValue("defaultAllowMessages", 0); //Default off
                    //    unset($settings);

                    // For version 5.1

                    $settings = new settings($dbo);
                    $settings->createValue("allowShowNotModeratedProfilePhotos", 1); //Default on
                    unset($settings);

                    // For version 5.2

                    $update->addColumnToUsersTable40();
                    $update->addColumnToUsersTable41();

                    $settings = new settings($dbo);
                    $settings->createValue("createChatsOnlyWithOTPVerified", 0); //Default off
                    unset($settings);

                    // For version 5.3

                    $update->addColumnToAdminsTable1();

                    // Add standard feelings

                    $feelings = new feelings($dbo);

                    if ($feelings->db_getMaxId() < 1) {

                        for ($i = 1; $i <= 12; $i++) {

                            $feelings->db_add(APP_URL."/feelings/".$i.".png");

                        }
                    }

                    // Add standard stickers

                    $stickers = new sticker($dbo);

                    if ($stickers->db_getMaxId() < 1) {

                        for ($i = 1; $i < 28; $i++) {

                            $stickers->db_add(APP_URL."/stickers/".$i.".png");

                        }
                    }

                    unset($stickers);

                    unset($update);

                    break;
                }
            }
        }
    }

    //

    $settings = new settings($dbo);
    $settings_array = $settings->get();

    //

    auth::newAuthenticityToken();

    $page_id = "update";

    $css_files = array("my.css");
    $page_title = APP_TITLE;

    include_once("html/common/site_header.inc.php");
?>

<body class="remind-page sn-hide">

    <?php

        include_once("html/common/site_topbar.inc.php");
    ?>

    <div class="wrap content-page">
        <div class="main-column">
            <div class="main-content">

                <div class="standard-page">

                    <?php

                        if (is_object($raccoonsquare_response)) {

                            if (!$raccoonsquare_response->error) {

                                switch ($raccoonsquare_response->error_code) {

                                    case ENVATO_ERROR_PCODE_INVALID: {

                                        // ENVATO_ERROR_PCODE_INVALID

                                        ?>
                                            <div class="error-container mt-3">
                                                <ul>
                                                    <b>Error!</b>
                                                    <br>
                                                    Invalid purchase code.
                                                </ul>
                                            </div>
                                        <?php

                                        break;
                                    }

                                    case ENVATO_ERROR_PCODE_ILLEGAL: {

                                        // ENVATO_ERROR_PCODE_ILLEGAL

                                        ?>
                                            <div class="error-container mt-3">
                                                <ul>
                                                    <b>Error!</b>
                                                    <br>
                                                    This purchase code is already in use for another domain.
                                                </ul>
                                            </div>
                                        <?php

                                        break;
                                    }

                                    case ENVATO_ERROR_PCODE_UNKNOWN: {

                                        // ENVATO_ERROR_PCODE_UNKNOWN

                                        ?>
                                            <div class="error-container mt-3">
                                                <ul>
                                                    <b>Error!</b>
                                                    <br>
                                                    We were unable to verify this purchase code. Try later..
                                                </ul>
                                            </div>
                                        <?php

                                        break;
                                    }

                                    default: {

                                        // All right!

                                        ?>
                                            <div class="success-container" style="margin-top: 15px;">
                                                <ul>
                                                    <b>Success!</b>
                                                    <br>
                                                    Your MySQL version: <?php echo $dbo->query('select version()')->fetchColumn(); ?>
                                                    <br>
                                                    Database refactoring success!
                                                </ul>
                                            </div>
                                        <?php
                                    }
                                }

                            } else {

                                // Invalid response from verifying purchase code envato server. Try later.

                                ?>
                                    <div class="error-container mt-3">
                                        <ul>
                                            <b>Error!</b>
                                            <br>
                                            Invalid response from verifying purchase code envato server. Try later.
                                            <br>
                                            Also check curl module - module must be installed and active.
                                        </ul>
                                    </div>
                                <?php
                            }

                        } else {

                            ?>
                                <form accept-charset="UTF-8" action="/update" class="custom-form" id="update-form" method="post">

                                    <input autocomplete="off" type="hidden" name="authenticity_token" value="<?php echo helper::getAuthenticityToken(); ?>">

                                    <div class="row">
                                        <div class="input-field col s12">
                                            <p style="font-weight: 600">To continue, you need to enter your purchase code. How to get purchase code you can read here: <br> <a style="text-decoration: underline" href="https://raccoonsquare.com/help/how_to_get_purchase_code/" target="_blank">How to get purchase code?</a></p>
                                        </div>
                                    </div>

                                    <input id="pcode" name="pcode" placeholder="Purchase code" required="required" size="32" type="text" value="">

                                    <div class="login-button">
                                        <input name="commit" type="submit" class="red button" value="Update">
                                    </div>

                                </form>
                            <?php
                        }
                    ?>

                </div>

            </div>
        </div>

    </div>

    <?php

        include_once("html/common/site_footer.inc.php");
    ?>

    <script>

        $('#update-form').submit(function(event) {

            event.preventDefault();

            grecaptcha.ready(function() {
                grecaptcha.execute('<?php echo RECAPTCHA_SITE_KEY; ?>', {action: 'submit'}).then(function(token) {

                    $('#update-form').prepend('<input type="hidden" name="recaptcha_token" value="'+ token + '">');
                    $('#update-form').unbind('submit').submit();
                });
            });
        });
    </script>

</body>
</html>