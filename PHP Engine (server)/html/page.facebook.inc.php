<?php

    /*!
     * ifsoft.co.uk
     *
     * http://ifsoft.com.ua, http://ifsoft.co.uk
     * raccoonsquare@gmail.com
     *
     * Copyright 2012-2020 Demyanchuk Dmitry (raccoonsquare@gmail.com)
     */

    if (auth::isSession()) {

        header("Location: /");
        exit;
    }

    if (isset($_SESSION['oauth']) && $_SESSION['oauth'] === 'facebook') {

        unset($_SESSION['oauth']);
        unset($_SESSION['oauth_id']);
        unset($_SESSION['oauth_name']);
        unset($_SESSION['oauth_email']);
        unset($_SESSION['oauth_link']);

        header("Location: /signup");
        exit;
    }

    header("Location: /");