<?php

/*!
 * ifsoft.co.uk
 *
 * http://ifsoft.com.ua, https://ifsoft.co.uk, https://raccoonsquare.com
 * raccoonsquare@gmail.com
 *
 * Copyright 2012-2020 Demyanchuk Dmitry (raccoonsquare@gmail.com)
 */

class report extends db_connect
{

	private $requestFrom = 0;

	public function __construct($dbo = NULL)
    {
		parent::__construct($dbo);
	}

    private function getMaxProfileReportId()
    {
        $stmt = $this->db->prepare("SELECT MAX(id) FROM profile_abuse_reports");
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

    private function getMaxProfilesReportId()
    {
        $stmt = $this->db->prepare("SELECT MAX(id) FROM profile_abuse_reports");
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

    public function removeComplaintToMember($abuseId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("DELETE FROM profile_abuse_reports WHERE id = (:abuseId)");
        $stmt->bindParam(":abuseId", $abuseId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $result = array("error" => false,
                            "error_code" => ERROR_SUCCESS);
        }

        return $result;
    }

    public function getComplaintToMember($abuseId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("SELECT * FROM profile_abuse_reports WHERE id = (:abuseId) LIMIT 1");
        $stmt->bindParam(":abuseId", $abuseId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                $row = $stmt->fetch();

                $result = array("error" => false,
                                "error_code" => ERROR_SUCCESS,
                                "id" => $row['id'],
                                "abuseFromUserId" => $row['abuseFromUserId'],
                                "abuseToUserId" => $row['abuseToUserId'],
                                "abuseId" => $row['abuseId'],
                                "createAt" => $row['createAt'],
                                "ip_addr" => $row['ip_addr']);
            }
        }

        return $result;
    }

    public function getComplaintsToMembers()
    {
        $complaints = array("error" => false,
                        "error_code" => ERROR_SUCCESS,
                        "id" => 0,
                        "complaints" => array());

        $stmt = $this->db->prepare("SELECT * FROM profile_abuse_reports ORDER BY id DESC");

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                while ($row = $stmt->fetch()) {

                    array_push($complaints['complaints'], $this->getComplaintToMember($row['id']));

                    $complaints['id'] = $row['id'];
                }
            }
        }

        return $complaints;
    }

    private function getMaxPhotoReportId()
    {
        $stmt = $this->db->prepare("SELECT MAX(id) FROM photo_abuse_reports");
        $stmt->execute();

        return $number_of_rows = $stmt->fetchColumn();
    }

    public function photo($photoId, $abuseId)
    {
        $result = array("error" => false,
                        "error_code" => ERROR_SUCCESS);

        $create_at = time();
        $ip_addr = helper::ip_addr();

        $stmt = $this->db->prepare("INSERT INTO photo_abuse_reports (abuseFromUserId, abuseToPhotoId, abuseId, createAt, ip_addr) value (:abuseFromUserId, :abuseToPhotoId, :abuseId, :createAt, :ip_addr)");
        $stmt->bindParam(":abuseFromUserId", $this->requestFrom, PDO::PARAM_INT);
        $stmt->bindParam(":abuseToPhotoId", $photoId, PDO::PARAM_INT);
        $stmt->bindParam(":abuseId", $abuseId, PDO::PARAM_INT);
        $stmt->bindParam(":createAt", $create_at, PDO::PARAM_INT);
        $stmt->bindParam(":ip_addr", $ip_addr, PDO::PARAM_STR);

        $stmt->execute();

        return $result;
    }

    public function getPhotoReports()
    {
        $reportId = $this->getMaxPhotoReportId();
        $reportId++;

        $result = array("error" => false,
                        "error_code" => ERROR_SUCCESS,
                        "reportId" => $reportId,
                        "reports" => array());

        $stmt = $this->db->prepare("SELECT id FROM photo_abuse_reports WHERE id < (:reportId) AND removeAt = 0 ORDER BY id DESC LIMIT 40");
        $stmt->bindParam(':reportId', $reportId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            while ($row = $stmt->fetch()) {

                $reportInfo = $this->photoReportInfo($row['id']);

                array_push($result['reports'], $reportInfo);

                $result['reportId'] = $reportInfo['id'];

                unset($reportInfo);
            }
        }

        return $result;
    }

    public function removePhotoReports($photoId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("DELETE FROM photo_abuse_reports WHERE abuseToPhotoId = (:photoId)");
        $stmt->bindParam(':photoId', $photoId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $result = array("error" => false,
                            "error_code" => ERROR_SUCCESS);
        }

        return $result;
    }

    public function removeAllPhotoReports()
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("DELETE FROM photo_abuse_reports");

        if ($stmt->execute()) {

            $result = array("error" => false,
                            "error_code" => ERROR_SUCCESS);
        }

        return $result;
    }

    public function photoReportInfo($reportId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("SELECT * FROM photo_abuse_reports WHERE id = (:reportId) LIMIT 1");
        $stmt->bindParam(":reportId", $reportId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                $row = $stmt->fetch();

                $photos = new photos($this->db);
                $photoInfo = $photos->info($row['abuseToPhotoId']);
                unset($photos);

                $profile = new profile($this->db,  $row['abuseFromUserId']);
                $profileInfo = $profile->getVeryShort();
                unset($profile);

                $result = array("error" => false,
                                "error_code" => ERROR_SUCCESS,
                                "id" => $row['id'],
                                "abuseFromUserId" => $row['abuseFromUserId'],
                                "abuseFromUserPhotoUrl" => $profileInfo['lowPhotoUrl'],
                                "abuseFromUserFullname" => $profileInfo['fullname'],
                                "abuseFromUserUsername" => $profileInfo['username'],
                                "abuseToPhotoId" => $row['abuseToPhotoId'],
                                "abuseToPhotoFromUserId" => $photoInfo['fromUserId'],
                                "abuseToPhotoFromUserPhotoUrl" => $photoInfo['fromUserPhoto'],
                                "abuseToPhotoFromUserFullname" => $photoInfo['fromUserFullname'],
                                "abuseToPhotoFromUserUsername" => $photoInfo['fromUserUsername'],
                                "abuseToPhotoUrl" => $photoInfo['imgUrl'],
                                "abuseToPhotoComment" => $photoInfo['comment'],
                                "abuseId" => $row['abuseId'],
                                "date" => date("Y-m-d H:i:s", $row['createAt']));
            }
        }

        return $result;
    }

    public function getProfilesReports($limit = 40)
    {
        $itemId = $this->getMaxProfilesReportId();
        $itemId++;

        $result = array("error" => false,
                        "error_code" => ERROR_SUCCESS,
                        "itemId" => $itemId,
                        "items" => array());

        $stmt = $this->db->prepare("SELECT id FROM profile_abuse_reports WHERE id < (:itemId) AND removeAt = 0 ORDER BY id DESC LIMIT :limit");
        $stmt->bindParam(':itemId', $itemId, PDO::PARAM_INT);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);

        if ($stmt->execute()) {

            while ($row = $stmt->fetch()) {

                $info = $this->profileReportInfo($row['id']);

                array_push($result['items'], $info);

                $result['itemId'] = $info['id'];

                unset($reportInfo);
            }
        }

        return $result;
    }

    public function getProfileReports($limit = 40)
    {
        $itemId = $this->getMaxProfileReportId();
        $itemId++;

        $result = array("error" => false,
            "error_code" => ERROR_SUCCESS,
            "itemId" => $itemId,
            "items" => array());

        $stmt = $this->db->prepare("SELECT * FROM profile_abuse_reports WHERE id < (:itemId) AND removeAt = 0 ORDER BY id DESC LIMIT :limit");
        $stmt->bindParam(':itemId', $itemId, PDO::PARAM_INT);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);

        if ($stmt->execute()) {

            while ($row = $stmt->fetch()) {

                $profile_info_abuseFromUserId = array("username" =>"username",
                    "fullname" => "fullname",
                    "verify" => 0,
                    "lowPhotoUrl" => "",
                    "online" => false);

                if ($row['abuseFromUserId'] != 0) {

                    $profile_abuseFromUserId = new profile($this->db, $row['abuseFromUserId']);
                    $profile_info_abuseFromUserId = $profile_abuseFromUserId->getVeryShort();
                }

                $profile_abuseToUserId = new profile($this->db, $row['abuseToUserId']);
                $profile_info_abuseToUserId = $profile_abuseToUserId->getVeryShort();

                $info = array("error" => false,
                            "error_code" => ERROR_SUCCESS,
                            "id" => $row['id'],
                            "abuseFromUserId" => $row['abuseFromUserId'],
                            "abuseFromUserUsername" => $profile_info_abuseFromUserId['username'],
                            "abuseFromUserFullname" => $profile_info_abuseFromUserId['fullname'],
                            "abuseFromUserVerified" => $profile_info_abuseFromUserId['verify'],
                            "abuseFromUserPhotoUrl" => $profile_info_abuseFromUserId['lowPhotoUrl'],
                            "abuseFromUserOnline" => $profile_info_abuseFromUserId['online'],
                            "abuseToUserId" => $row['abuseToUserId'],
                            "abuseToUserUsername" => $profile_info_abuseToUserId['username'],
                            "abuseToUserFullname" => $profile_info_abuseToUserId['fullname'],
                            "abuseToUserVerified" => $profile_info_abuseToUserId['verify'],
                            "abuseToUserPhotoUrl" => $profile_info_abuseToUserId['lowPhotoUrl'],
                            "abuseToUserOnline" => $profile_info_abuseToUserId['online'],
                            "abuseId" => $row['abuseId'],
                            "date" => date("Y-m-d H:i:s", $row['createAt']));

                array_push($result['items'], $info);

                $result['itemId'] = $info['id'];

                unset($profile_info_abuseToUserId);
                unset($profile_info_abuseFromUserId);
                unset($info);
            }
        }

        return $result;
    }

    public function profileReportInfo($itemId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("SELECT * FROM profile_abuse_reports WHERE id = (:itemId) LIMIT 1");
        $stmt->bindParam(":itemId", $itemId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            if ($stmt->rowCount() > 0) {

                $row = $stmt->fetch();

                $result = array("error" => false,
                                "error_code" => ERROR_SUCCESS,
                                "id" => $row['id'],
                                "abuseFromUserId" => $row['abuseFromUserId'],
                                "abuseToUserId" => $row['abuseToUserId'],
                                "abuseId" => $row['abuseId'],
                                "date" => date("Y-m-d H:i:s", $row['createAt']));
            }
        }

        return $result;
    }

    public function removeProfileReports($itemId)
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("DELETE FROM profile_abuse_reports WHERE abuseToUserId = (:itemId)");
        $stmt->bindParam(':itemId', $itemId, PDO::PARAM_INT);

        if ($stmt->execute()) {

            $result = array("error" => false,
                            "error_code" => ERROR_SUCCESS);
        }

        return $result;
    }

    public function removeAllProfilesReports()
    {
        $result = array("error" => true,
                        "error_code" => ERROR_UNKNOWN);

        $stmt = $this->db->prepare("DELETE FROM profile_abuse_reports");

        if ($stmt->execute()) {

            $result = array("error" => false,
                            "error_code" => ERROR_SUCCESS);
        }

        return $result;
    }

    public function setRequestFrom($requestFrom)
    {
        $this->requestFrom = $requestFrom;
    }

    public function getRequestFrom()
    {
        return $this->requestFrom;
    }
}

