<?php

/*!
 * ifsoft.co.uk
 *
 * http://ifsoft.com.ua, http://ifsoft.co.uk
 * raccoonsquare@gmail.com
 *
 * Copyright 2012-2019 Demyanchuk Dmitry (raccoonsquare@gmail.com)
 */

if (!empty($_POST)) {

    $accountId = isset($_POST['accountId']) ? $_POST['accountId'] : '';
    $accessToken = isset($_POST['accessToken']) ? $_POST['accessToken'] : '';

    $currentPassword = isset($_POST['currentPassword']) ? $_POST['currentPassword'] : '';

    $currentPassword = helper::clearText($currentPassword);
    $currentPassword = helper::escapeText($currentPassword);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        api::printError(ERROR_ACCESS_TOKEN, "Error authorization.");
    }

    $result = array("error" => true,
                    "error_code" => ERROR_UNKNOWN);

    // Remove All Medias

    $gallery = new gallery($dbo);
    $gallery->setRequestFrom($accountId);
    $gallery->removeAll();
    unset($gallery);

    $account = new account($dbo, $accountId);

    // Remove Avatar

    $photos = array("error" => false,
                    "originPhotoUrl" => "",
                    "normalPhotoUrl" => "",
                    "bigPhotoUrl" => "",
                    "lowPhotoUrl" => "");

    $account->setPhoto($photos);

    // Unset Facebook Id

    $account->setFacebookId("");

    // Unset Email

    $account->setEmail("");

    // Deactivate Account

    $result = $account->deactivation($currentPassword);

    echo json_encode($result);
    exit;
}
