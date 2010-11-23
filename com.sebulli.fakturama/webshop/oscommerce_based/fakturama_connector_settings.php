<?php

/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
 * 
 * 
 * Web shop connector script settings
 *
 * Version 1.1.0
 * Date: 2010-11-20
 * 
 * 
 *
 * Copyright (C) 2010 Gerd Bartelt
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Bartelt - initial API and implementation
 */
  
// Define Shop system. Allowed values are:
// 'OSCOMMERCE'		// osCommerce	2.2 RC2a		www.oscommerce.com
// 'XTCOMMERCE'		// xt:Commerce	3.04 SP2.1		www.xt-commerce.com
// 'XTCMODIFIED'	// xtcModified	1.04			www.xtc-modified.org
define ('FAKTURAMA_WEBSHOP','XTCMODIFIED');	

// Define user name and password
define ('FAKTURAMA_USERNAME', 'user');		
define ('FAKTURAMA_PASSWORD', 'password');	

// Language code of the product categorie which will be imported.
// (en = English, de = German, es = Spanish ..) 
define ('FAKTURAMA_LANGUAGE_CODE', 'de');			

// ISO 3166-1-alpha-2 country code of the country of the web shop.
// (US = USA, GB = United Kingdom, DE = Germany ..) 
define ('FAKTURAMA_COUNTRY', 'DE');				

// Language of the notification email 
// Name must be in lower case and must match a folder name under /admin/includes/languages
// (english = English, german = German, espanol = Spanish ..) 
define ('FAKTURAMA_LANGUAGE', 'german');			

?>
