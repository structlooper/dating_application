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

    $clientId = isset($_POST['clientId']) ? $_POST['clientId'] : 0;

    $accountId = isset($_POST['accountId']) ? $_POST['accountId'] : 0;
    $accessToken = isset($_POST['accessToken']) ? $_POST['accessToken'] : '';

    $itemId = isset($_POST['itemId']) ? $_POST['itemId'] : 0;

    $clientId = helper::clearInt($clientId);
    $accountId = helper::clearInt($accountId);

    $itemId = helper::clearInt($itemId);

    $result = array("error" => true,
                    "error_code" => ERROR_UNKNOWN);

    $auth = new auth($dbo);

    if (!$auth->authorize($accountId, $accessToken)) {

        api::printError(ERROR_ACCESS_TOKEN, "Error authorization.");
    }

    $photos = new photos($dbo);
    $photos->setRequestFrom($accountId);

    $itemInfo = $photos->info($itemId);

    if (!$itemInfo['error']) {

        if ($itemInfo['fromUserId'] != $accountId && $itemInfo['removeAt'] != 0) {

            exit;
        }

        $images = new images($dbo);
        $images->setRequestFrom($accountId);

        $result = array("error" => false,
                        "error_code" => ERROR_SUCCESS,
                        "itemId" => $itemId,
                        "comments" => $images->commentsGet($itemId, 0),
                        "items" => array());

        array_push($result['items'], $itemInfo);
    }

    echo json_encode($result);
    exit;
}
