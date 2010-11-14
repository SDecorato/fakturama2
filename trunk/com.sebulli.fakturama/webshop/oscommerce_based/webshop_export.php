<?php

/* 
 * Fakturama - Free Invoicing Software - http://fakturama.sebulli.com
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

// Use the settings from webshop_export_settings.php, if it exists.
if( file_exists('webshop_export_settings.php')) {
    include('webshop_export_settings.php');
}

// Define Shop system. Allowed values are:
// 'OSCOMMERCE'		// osCommerce	2.2 RC2a		www.oscommerce.com
// 'XTCOMMERCE'		// xt:Commerce	3.04 SP2.1		www.xt-commerce.com
// 'XTCMODIFIED'	// xtcModified	1.05 ..			www.xtc-modified.org
define ('FAKTURAMA_WEBSHOP','XTCMODIFIED');	


/***********************************************************************************************************************************************/
/***********************************************************************************************************************************************/
/***********************************************************************************************************************************************/
/***********************************************************************************************************************************************/
/***********************************************************************************************************************************************/



// Some shop systems are based on osCommerce, some on xtCommerce
if (FAKTURAMA_WEBSHOP == OSCOMMERCE) {
	define ('FAKTURAMA_WEBSHOP_BASE','OSCOMMERCE');	
} 
else if (FAKTURAMA_WEBSHOP == XTCOMMERCE) {
	define ('FAKTURAMA_WEBSHOP_BASE','XTCOMMERCE');	
}
else if (FAKTURAMA_WEBSHOP == XTCMODIFIED) {
	define ('FAKTURAMA_WEBSHOP_BASE','XTCOMMERCE');	
}



// Set the level of error reporting
error_reporting(E_ALL & ~E_NOTICE);

// check support for register_globals
if (function_exists('ini_get') && (ini_get('register_globals') == false) && (PHP_VERSION < 4.3) ) {
	exit('Server Requirement Error: register_globals is disabled in your PHP configuration. This can be enabled in your php.ini configuration file or in the .htaccess file in your catalog directory. Please use PHP 4.3+ if register_globals cannot be enabled on the server.');
}


// Include application configuration parameters
require('includes/configure.php');

if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) {
	
	// Define the project version
	define('PROJECT_VERSION', 'osCommerce Online Merchant v2.x');
	// some code to solve compatibility issues
	require(DIR_WS_FUNCTIONS . 'compatibility.php');
	
	define('LANG_DIR','../includes/languages/');

	// set php_self in the local scope
	$PHP_SELF = (isset($HTTP_SERVER_VARS['PHP_SELF']) ? $HTTP_SERVER_VARS['PHP_SELF'] : $HTTP_SERVER_VARS['SCRIPT_NAME']);
	
	// include the database functions
	require(DIR_WS_FUNCTIONS . 'database.php');
}

if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) {
  // security
  define('_VALID_XTC',true);

  // Set the level of error reporting
  error_reporting(E_ALL & ~E_NOTICE);

  // Disable use_trans_sid as xtc_href_link() does this manually
  if (function_exists('ini_set')) {
    ini_set('session.use_trans_sid', 0);
  }

  define('LANG_DIR','../lang/');
  
//  define('SQL_CACHEDIR',DIR_FS_CATALOG.'cache/');

  // Define the project version
  define('PROJECT_VERSION', 'xt:Commerce v 3.x');


  // Include required functions
  require_once(DIR_FS_INC . 'xtc_db_connect.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_close.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_error.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_query.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_queryCached.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_perform.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_fetch_array.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_num_rows.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_free_result.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_fetch_fields.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_output.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_input.inc.php');
  require_once(DIR_FS_INC . 'xtc_db_prepare_input.inc.php');
  require_once(DIR_FS_INC . 'xtc_not_null.inc.php');
  require(DIR_FS_CATALOG.DIR_WS_CLASSES . 'Smarty_2.6.26/Smarty.class.php');
  require_once (DIR_FS_CATALOG.DIR_WS_CLASSES.'class.phpmailer.php');
  require_once (DIR_FS_INC.'xtc_php_mail.inc.php');

}

function sbf_not_null($p) {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_not_null($p);
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_not_null($p);
}

function sbf_db_connect() {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_db_connect();
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_db_connect();
}

function sbf_db_query($p) {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_db_query($p);
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_db_query($p);
}

function sbf_db_fetch_array($p) {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_db_fetch_array($p);
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_db_fetch_array($p);
}

function sbf_db_prepare_input($p) {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_db_prepare_input($p);
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_db_prepare_input($p);
}

function sbf_db_input($p) {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_db_input($p);
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_db_input($p);
}

function sbf_db_output($p) {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_db_output($p);
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_db_output($p);
}

function sbf_db_num_rows($p) {
	if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) return tep_db_num_rows($p);
	if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) return xtc_db_num_rows($p);
}




// include the mail classes
function sbf_php_mail($from_email_address, $from_email_name, $to_email_address, $to_name, $forwarding_to, $reply_address, $reply_address_name, $path_to_attachement, $path_to_more_attachements, $email_subject, $message_body_html, $message_body_plain) {
	global $mail_error;

$mailsmarty= new Smarty;
$mailsmarty->compile_dir = DIR_FS_DOCUMENT_ROOT.'templates_c';

// Check for existing signature files
// load the signatures only, if the appropriate file(s) exists
	$html_signatur = '';
	$txt_signatur = '';
  if (file_exists(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/signatur.html')) {
        $html_signatur = $mailsmarty->fetch(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/signatur.html');
  }
  if (file_exists(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/signatur.txt')) {
        $txt_signatur = $mailsmarty->fetch(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/signatur.txt');
  }

  //Widerruf in Email
  $html_widerruf = '';
  $txt_widerruf = '';
  if (file_exists(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/widerruf.html')) {
        $html_widerruf = $mailsmarty->fetch(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/widerruf.html');
  }
  if (file_exists(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/widerruf.txt')) {
        $txt_widerruf = $mailsmarty->fetch(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/mail/'.$_SESSION['language'].'/widerruf.txt');
  }
  
  //Platzhalter [WIDERRUF] durch Widerruf Text ersetzen
  if (strpos($message_body_html,'[WIDERRUF]') !== false) {
	$message_body_html = str_replace('[WIDERRUF]', $html_widerruf, $message_body_html);
  }
  if (strpos($message_body_plain,'[WIDERRUF]') !== false) {
	$message_body_plain = str_replace('[WIDERRUF]', $txt_widerruf, $message_body_plain);
  }
  
  //Platzhalter [SIGNATUR] durch Signatur Text ersetzen
  if (strpos($message_body_html,'[SIGNATUR]') !== false) {
	$message_body_html = str_replace('[SIGNATUR]', $html_signatur, $message_body_html);
	$html_signatur = '';
  }
  if (strpos($message_body_plain,'[SIGNATUR]') !== false) {
	$message_body_plain = str_replace('[SIGNATUR]', $txt_signatur, $message_body_plain);
	$txt_signatur = '';
  }

//**********************************************************************************************

	$mail = new PHPMailer();
	$mail->PluginDir = DIR_FS_DOCUMENT_ROOT.'includes/classes/';

	if (isset ($_SESSION['language_charset'])) {
		$mail->CharSet = $_SESSION['language_charset'];
	} else {
		$lang_query = "SELECT * FROM languages WHERE code = '".DEFAULT_LANGUAGE."'";
		$lang_query = xtc_db_query($lang_query);
		$lang_data = xtc_db_fetch_array($lang_query);
		$mail->CharSet = $lang_data['language_charset'];		
	}
	//SetLanguage Multilanguage
    if (isset (	$_SESSION['language_code'])) {		
		$lang_code = $_SESSION['language_code'];
	} else $lang_code = DEFAULT_LANGUAGE;	
	
	$mail->SetLanguage($lang_code, DIR_WS_CLASSES);	
	
	if (EMAIL_TRANSPORT == 'smtp') {
		$mail->IsSMTP();
		$mail->SMTPKeepAlive = true; // set mailer to use SMTP
		$mail->SMTPAuth = SMTP_AUTH; // turn on SMTP authentication true/false
		$mail->Username = SMTP_USERNAME; // SMTP username
		$mail->Password = SMTP_PASSWORD; // SMTP password
		$mail->Host = SMTP_MAIN_SERVER.';'.SMTP_Backup_Server; // specify main and backup server "smtp1.example.com;smtp2.example.com"
	}

	if (EMAIL_TRANSPORT == 'sendmail') { // set mailer to use SMTP
		$mail->IsSendmail();
		$mail->Sendmail = SENDMAIL_PATH;
	}
	if (EMAIL_TRANSPORT == 'mail') {
		$mail->IsMail();
	}

	if (EMAIL_USE_HTML == 'true') // set email format to HTML
		{
		$mail->IsHTML(true);
		$mail->Body = $message_body_html.$html_signatur;//DPW Signatur ergänzt.
		// remove html tags
		$message_body_plain = str_replace('<br />', " \n", $message_body_plain.$txt_signatur);//DPW Signatur ergänzt.
		$message_body_plain = strip_tags($message_body_plain);
		$mail->AltBody = $message_body_plain;
	} else {
		$mail->IsHTML(false);
		//remove html tags
		$message_body_plain = str_replace('<br />', " \n", $message_body_plain.$txt_signatur);//DPW Signatur ergänzt.
		$message_body_plain = strip_tags($message_body_plain);
		$mail->Body = $message_body_plain;
	}

	$mail->From = $from_email_address;
	$mail->Sender = $from_email_address;
	$mail->FromName = $from_email_name;
	$mail->AddAddress($to_email_address, $to_name);
	if ($forwarding_to != '')
		$mail->AddBCC($forwarding_to);
	$mail->AddReplyTo($reply_address, $reply_address_name);

	$mail->WordWrap = 50; // set word wrap to 50 characters
	$mail->Subject = $email_subject;

	if (!$mail->Send())
	  return "Error sending email to: \"" . $to_email_address . "\" - " . $mail->ErrorInfo;
	
	return "";
}





// Output a raw date string in the selected locale date format
// $raw_date needs to be in this format: YYYY-MM-DD HH:MM:SS
  function sbf_date_long($raw_date) {
    if ( ($raw_date == '0000-00-00 00:00:00') || ($raw_date == '') ) return false;

    $year = (int)substr($raw_date, 0, 4);
    $month = (int)substr($raw_date, 5, 2);
    $day = (int)substr($raw_date, 8, 2);
    $hour = (int)substr($raw_date, 11, 2);
    $minute = (int)substr($raw_date, 14, 2);
    $second = (int)substr($raw_date, 17, 2);

    return strftime('%A, %d. %B %Y', mktime($hour,$minute,$second,$month,$day,$year));

  }

// make a connection to the database... now
sbf_db_connect() or die('Unable to connect to database server!');




// set application wide parameters
$configuration_query = sbf_db_query("SELECT
										configuration_key AS cfgKey, configuration_value AS cfgValue
									 FROM
									 	 configuration");
									 	 
while ($configuration = sbf_db_fetch_array($configuration_query)) {
	$configuration_array[$configuration['cfgKey']] = $configuration['cfgValue'];
	define($configuration['cfgKey'], $configuration['cfgValue']);
}

// Define our general functions used application-wide
require(DIR_WS_FUNCTIONS . 'general.php');
require(DIR_WS_FUNCTIONS . 'html_output.php');

// Return true if $str ends with $sub
function endsWith( $str, $sub ) {
	return ( substr( $str, strlen( $str ) - strlen( $sub ) ) == $sub );
}

//Return true if $str starts with $sub
function startsWith($str, $sub){ 
    return substr($str, 0, strlen($sub)) == $sub;
}

// Convert a string to UTF-8 and replace the qotes
function my_encode($s) {

	// Convert a string to UTF-8 and do not replace the qotes
	$s = my_encode_with_quotes($s);

	// Replace quotes
	$s = str_replace("\"", "&quot;", $s);
	return $s;
}

// Convert a string to UTF-8 and do not replace the qotes
function my_encode_with_quotes($s) {

	// Convert to UTF-8
	$s = utf8_encode($s);
	
	// Convert entities like &uuml; to ü
	$s = html_entity_decode($s, ENT_COMPAT , "UTF-8");

	// Replace ampersand
	$s = str_replace("&", "&amp;", $s);

	return $s;
}

// Remove the HTML tags but keep the BR-tags
function my_strip_tags($s) {
	
	// Remove all HTML tags	
	$s = strip_tags($s);

	// But keep the BR-tags
	$s = str_replace("\n", "<br />", $s);
	$s = str_replace("\r", "", $s);
	return $s;
}


class order {
    var $info, $totals, $products, $customer, $delivery;

    function order($order_id) {
      $this->info = array();
      $this->totals = array();
      $this->products = array();
      $this->customer = array();
      $this->delivery = array();

      $this->query($order_id);
    }

    function query($order_id) {
    
      $order_query_payment_class = "";
      if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE)
      	$order_query_payment_class = "payment_class, ";
      $order_query = sbf_db_query("SELECT
      									customers_id, customers_cid, customers_name, customers_company, customers_street_address,
      									customers_suburb, customers_city, customers_postcode, customers_state,
      									customers_country, customers_telephone, customers_email_address, customers_address_format_id,
      									delivery_name, delivery_company, delivery_street_address, delivery_suburb, delivery_city,
      									delivery_postcode, delivery_state, delivery_country, delivery_address_format_id,
      									billing_name, billing_company, billing_street_address, billing_suburb, billing_city, billing_postcode,
      									billing_state, billing_country, billing_address_format_id, payment_method,".$order_query_payment_class."
      									cc_type, cc_owner, cc_number, cc_expires, currency, currency_value, date_purchased,
      									orders_status, last_modified, language
      								FROM
      									orders
      								WHERE
      									orders_id = '" . (int)$order_id . "'
      								");
      								
      $order = sbf_db_fetch_array($order_query);

      $totals_query = sbf_db_query("SELECT
      									title, text
      								FROM 
      									orders_total
      								WHERE
      									orders_id = '" . (int)$order_id . "'
      								ORDER BY
      									sort_order
      								");
      								
      while ($totals = sbf_db_fetch_array($totals_query)) {
        $this->totals[] = array('title' => $totals['title'],
                                'text' => $totals['text']);
      }

      $this->info = array('currency' => $order['currency'],
                          'currency_value' => $order['currency_value'],
                          'payment_method' => $order['payment_method'],
                          'payment_class' => $order['payment_class'],
                          'cc_type' => $order['cc_type'],
                          'cc_owner' => $order['cc_owner'],
                          'cc_number' => $order['cc_number'],
                          'cc_expires' => $order['cc_expires'],
                          'date_purchased' => $order['date_purchased'],
                          'orders_status' => $order['orders_status'],
                          'language' => $order['language'],
                          'last_modified' => $order['last_modified']);

      $this->customer = array(
      						  'id' => $order['customers_id'],
      						  'cid' => $order['customers_cid'],
      						  'firstname' => "",
      						  'lastname' => $order['customers_name'],
      						  'name' => $order['customers_name'],
                              'company' => $order['customers_company'],
                              'street_address' => $order['customers_street_address'],
                              'suburb' => $order['customers_suburb'],
                              'city' => $order['customers_city'],
                              'postcode' => $order['customers_postcode'],
                              'state' => $order['customers_state'],
                              'country' => $order['customers_country'],
                              'format_id' => $order['customers_address_format_id'],
                              'telephone' => $order['customers_telephone'],
                              'email_address' => $order['customers_email_address']);

      $this->delivery = array('name' => $order['delivery_name'],
      						  'firstname' => "",
      						  'lastname' => $order['delivery_name'],
      						  'gender' => "",
                              'company' => $order['delivery_company'],
                              'street_address' => $order['delivery_street_address'],
                              'suburb' => $order['delivery_suburb'],
                              'city' => $order['delivery_city'],
                              'postcode' => $order['delivery_postcode'],
                              'state' => $order['delivery_state'],
                              'country' => $order['delivery_country'],
                              'format_id' => $order['delivery_address_format_id']);

      $this->billing = array('name' => $order['billing_name'],
      						 'firstname' => "",
      						 'lastname' => $order['billing_name'],
      						 'gender' => "",
                             'company' => $order['billing_company'],
                             'street_address' => $order['billing_street_address'],
                             'suburb' => $order['billing_suburb'],
                             'city' => $order['billing_city'],
                             'postcode' => $order['billing_postcode'],
                             'state' => $order['billing_state'],
                             'country' => $order['billing_country'],
                             'format_id' => $order['billing_address_format_id']);
                             
      $customers_id = $this->customer['id'];
      $firstandlastname = $this->customer['firstname'] . " " . $this->customer['lastname'] . "-";                       


      $orders_address_query = sbf_db_query("SELECT
      											customers_id, entry_gender, entry_firstname, entry_lastname
      										FROM
      											address_book
      										WHERE
      											customers_id = '" . (int)$customers_id . "'
      										");

     while ($orders_address = sbf_db_fetch_array($orders_address_query)) {
		$firstandlastname = $orders_address['entry_firstname'] . " " . $orders_address['entry_lastname'];         
		if ($firstandlastname == $this->billing['name']) {
			$this->billing['firstname'] = $orders_address['entry_firstname'];
			$this->billing['lastname'] = $orders_address['entry_lastname'];
			$this->billing['gender'] = $orders_address['entry_gender'];
			
		}             
		if ($firstandlastname == $this->delivery['name']) {
			$this->delivery['firstname'] = $orders_address['entry_firstname'];
			$this->delivery['lastname'] = $orders_address['entry_lastname'];
			$this->delivery['gender'] = $orders_address['entry_gender'];
		}             
     }
                                        



                             
                             

      $index = 0;

     										
     $orders_products_query = sbf_db_query("SELECT
     											tax.tax_description, ordprod.orders_products_id, ordprod.products_name,ordprod.products_id,
     											ordprod.products_model, ordprod.products_price, ordprod.products_tax,
     											ordprod.products_quantity, ordprod.final_price 
     										FROM
     											tax_rates tax 
     											RIGHT JOIN
     											products prod ON (prod.products_tax_class_id = tax.tax_rates_id)
     											RIGHT JOIN 
     											orders_products ordprod	ON (prod.products_id = ordprod.products_id) 
     										WHERE 
     											ordprod.orders_id = '" . (int)$order_id . "'
     										");
     										

	$language_query = sbf_db_query("SELECT
       									langu.code
    								FROM
  										languages langu
  									ORDER BY
  										languages_id ASC
        							");
        							
	$category_language = "";
	while ($category_languages = sbf_db_fetch_array($language_query)) {
		if (empty ($category_language))
			$category_language = $category_languages['code'];
		if (FAKTURAMA_LANGUAGE == $category_languages['code'])
			$category_language = $category_languages['code'];
    }
 
      
      while ($orders_products = sbf_db_fetch_array($orders_products_query)) {
        $this->products[$index] = array(
								        'id' => $orders_products['orders_products_id'],
								        'qty' => $orders_products['products_quantity'],
                                        'name' => $orders_products['products_name'],
                                        'products_id' => $orders_products['products_id'],
                                        'model' => $orders_products['products_model'],
                                        'tax' => $orders_products['products_tax'],
                                        'tax_description' => $orders_products['tax_description'],
                                        'price' => $orders_products['products_price'],
                                        'final_price' => $orders_products['final_price']);
                                        
                                        
        $category_query = sbf_db_query("SELECT
        								  		cat_desc.categories_name, langu.code , cat_desc.categories_id , prod_cat.products_id
        								  FROM
        								    	products_to_categories prod_cat 
										  RIGHT JOIN
  												categories_description cat_desc ON (prod_cat.categories_id = cat_desc.categories_id)
										  LEFT JOIN
  												languages langu ON (langu.languages_id = cat_desc.language_id)
        								  WHERE 
        								  		prod_cat.products_id = '" . (int)$orders_products['products_id'] . "'
        								  		AND langu.code ='". $category_language ."' 
        								  ");

		$category = "";
		if ($orders_category = sbf_db_fetch_array($category_query)) {
			$category = $orders_category['categories_name'];
     	}
     	$this->products[$index]['category'] = $category;
        	
       								  
                                        

        $subindex = 0;
        $attributes_query = sbf_db_query("SELECT
        								  		products_options, products_options_values, options_values_price, price_prefix
        								  FROM
        								  		orders_products_attributes
        								  WHERE 
        								  		orders_id = '" . (int)$order_id . "' 
        								  		AND orders_products_id = '" . (int)$orders_products['orders_products_id'] . "'"
        								  );
        if (sbf_db_num_rows($attributes_query)) {
          while ($attributes = sbf_db_fetch_array($attributes_query)) {
            $this->products[$index]['attributes'][$subindex] = array('option' => $attributes['products_options'],
                                                                     'value' => $attributes['products_options_values'],
                                                                     'prefix' => $attributes['price_prefix'],
                                                                     'price' => $attributes['options_values_price']);

            $subindex++;
          }
        }
        $index++;
      }
    }
}
  
  



// load the installed payment module
if (defined('MODULE_PAYMENT_INSTALLED') && sbf_not_null(MODULE_PAYMENT_INSTALLED)) {
	$modules_payment = explode(';', MODULE_PAYMENT_INSTALLED);

	$include_modules_payment = array();

	if ( (sbf_not_null($module)) && (in_array($module . '.' . substr($PHP_SELF, (strrpos($PHP_SELF, '.')+1)), $modules_payment)) ) {
		$selected_module = $module;

		$include_modules_payment[] = array('class' => $module, 'file' => $module . '.php');
	} else {
		reset($modules_payment);
		while (list(, $value) = each($modules_payment)) {
			$class = substr($value, 0, strrpos($value, '.'));
			$include_modules_payment[] = array('class' => $class, 'file' => $value);
		}
	}
}


// load the installed shipping module
if (defined('MODULE_SHIPPING_INSTALLED') && sbf_not_null(MODULE_SHIPPING_INSTALLED)) {
	$modules_shipping = explode(';', MODULE_SHIPPING_INSTALLED);

	$include_modules_shipping = array();

	if ( (sbf_not_null($module)) && (in_array($module . '.' . substr($PHP_SELF, (strrpos($PHP_SELF, '.')+1)), $modules_shipping)) ) {
		$selected_module = $module;

		$include_modules_shipping[] = array('class' => $module, 'file' => $module . '.php');
	} else {
		reset($modules_shipping);
		while (list(, $value) = each($modules_shipping)) {
			$class = substr($value, 0, strrpos($value, '.'));
			$include_modules_shipping[] = array('class' => $class, 'file' => $value);
		}
	}
}



// search all languages for the payment method
$languages_query = sbf_db_query("SELECT
									directory
								 FROM
								 	languages
								 ");
while ($languages = sbf_db_fetch_array($languages_query)) {
	for ($i=0, $n=sizeof($include_modules_payment); $i<$n; $i++) {
		$filename = LANG_DIR . $languages[directory] . '/modules/payment/' . $include_modules_payment[$i]['file'];	

		if (file_exists($filename)) {
			$paymentfile = fopen($filename,'r');
			while (!feof($paymentfile)){ 
				$zeile = fgets($paymentfile,1024);
	
				$pos1 = strpos($zeile, "('MODULE_PAYMENT_");
				$pos2 = strpos($zeile, "_TEXT_TITLE'");
				if ( ($pos1 > 0) && ($pos2 > 0)){
					$paymenttext = substr ( $zeile, $pos2 + 13 );
					$paymenttext = substr ( $paymenttext,strpos($paymenttext, "'")+1 );
					$paymenttext = substr ( $paymenttext, 0, strrpos($paymenttext, "'") );
					$paymenttext = trim ($paymenttext);
					if ($paymenttext) {
						if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE)
							$paymentsynonym[$paymenttext] = $include_modules_payment[$i]['class'];
						if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE)
							$paymentsynonym[$include_modules_payment[$i]['class']] = $paymenttext;
					}
				}
			} 
			fclose($paymentfile); 
		}
	}
}

// search all shippings for the shipping method
$languages_query = sbf_db_query("SELECT
									directory
								 FROM
								 	languages
								 ");
while ($languages = sbf_db_fetch_array($languages_query)) {
	for ($i=0, $n=sizeof($include_modules_shipping); $i<$n; $i++) {
		$filename = LANG_DIR . $languages[directory] . '/modules/shipping/' . $include_modules_shipping[$i]['file'];	
		if (file_exists($filename)) {
			$shippingfile = fopen($filename,'r'); 
			while (!feof($shippingfile)){ 
				$zeile = fgets($shippingfile,1024);
	
				$pos1 = strpos($zeile, "('MODULE_SHIPPING_");
				$pos2 = strpos($zeile, "_TEXT_TITLE'");
				if ( ($pos1 > 0) && ($pos2 > 0)){
					$shippingtext = substr ( $zeile, $pos2 + 13 );
					$shippingtext = substr ( $shippingtext,strpos($shippingtext, "'")+1 );
					$shippingtext = substr ( $shippingtext, 0, strrpos($shippingtext, "'") );
					$shippingtext = trim ($shippingtext);
					if ($shippingtext)
						$shippingssynonym[$shippingtext] = $include_modules_shipping[$i]['class'];
				}
			} 
			fclose($shippingfile); 
		}
	}
}


// parse POST parameters
$getshipped = (isset($HTTP_POST_VARS['getshipped']) ? $HTTP_POST_VARS['getshipped'] : '');
$action = (isset($HTTP_POST_VARS['action']) ? $HTTP_POST_VARS['action'] : '');
$orderstosync = (isset($HTTP_POST_VARS['setstate']) ? $HTTP_POST_VARS['setstate'] : '{}');


// does action start with "get" ?
if (strncmp($action, "get", 3)) {
  // does the action contains one of the following keys:
  $action_getproducts = strpos($action,"products");
  $action_getorders = strpos($action,"orders");
  $action_getcontacts = strpos($action,"contacts");
}


$orderstosync = substr($orderstosync, 0, -1);
$orderstosync = substr($orderstosync, 1);
$orderstosync = explode(",", $orderstosync);

$username = sbf_db_prepare_input($HTTP_POST_VARS['username']);
$password = sbf_db_prepare_input($HTTP_POST_VARS['password']);


// generate header of response
echo ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
echo ("<webshopexport version=\"1.0\" >\n");
echo ("<webshop ");

if (FAKTURAMA_WEBSHOP == OSCOMMERCE)
	echo ("shop=\"osCommerce\" ");
else if (FAKTURAMA_WEBSHOP == XTCOMMERCE)
	echo ("shop=\"xt:Commerce\" ");
else if (FAKTURAMA_WEBSHOP == XTCMODIFIED)
	echo ("shop=\"xtcModified\" ");
else
	echo ("shop=\"???\" ");
echo ("url=\"" . HTTP_CATALOG_SERVER . "\"");	
echo ("></webshop>\n");


if (strncmp($action, "get", 3) == 0) {
  // does the action contains one of the following keys:
  $action_getproducts = strpos($action,"products");
  $action_getorders = strpos($action,"orders");
  $action_getcontacts = strpos($action,"contacts");
}



// parse the GETSHIPPED parameter for the time interval
$getshipped = strtolower($getshipped);

if (preg_match('/\d+/', $getshipped, $matches)){
	$getshipped_number = $matches[0];
}

if (preg_match('/month|day|week|year|ever/', $getshipped, $matches)){
	$getshipped_datetype = $matches[0];
}

if (($getshipped_number > 0) && ($getshipped_datetype))
	$getshipped_condition = " or ( DATE_SUB(CURDATE(),INTERVAL ". $getshipped_number ." ". $getshipped_datetype." ) <= o.date_purchased) ";

if ($getshipped_datetype == 'ever')
	$getshipped_condition = " or TRUE";



// Get the selected language or at least the 
$language_query = sbf_db_query("SELECT
   									langu.code
   								FROM
									languages langu
								ORDER BY
									languages_id ASC
       							");
        							
$category_language = "en";
while ($category_languages = sbf_db_fetch_array($language_query)) {
	if (empty ($category_language))
		$category_language = $category_languages['code'];
	if (FAKTURAMA_LANGUAGE == $category_languages['code'])
		$category_language = $category_languages['code'];
}


if ( ( FAKTURAMA_USERNAME == $username) && ( FAKTURAMA_PASSWORD == $password) ){

		//password ok


	// update the shop values
	foreach ($orderstosync as $ordertosync) {

 	    list($orders_id_tosync, $orders_status_tosync) = explode("=", trim($ordertosync));

	    if ($orders_status_tosync == 'pending')    $orders_status_tosync = 1;
	    if ($orders_status_tosync == 'processing') $orders_status_tosync = 2;
	    if ($orders_status_tosync == 'shipped')    $orders_status_tosync = 3;

	    $customer_notified = 0;

	    // Notify the customer
	    $notify_comments = '';
	    // Is there a comment ?
	    if (strlen ($orders_status_tosync) > 1) {
		$notify_comments = substr($orders_status_tosync,1);
	    }
 		
	    if (startsWith($notify_comments,"*")) {
	    
	    // First character is the new status
	    $orders_status_tosync = substr($orders_status_tosync,0,1);
		
            // Remove the "*"
	    $notify_comments = substr($notify_comments,1);

	    // Convert it into the correct character encoding
	    $notify_comments_mail = iconv("UTF-8", FAKTURAMA_MAIL_ENCODING, $notify_comments);

	    
	    $order = new order($orders_id_tosync);
	    $smarty = new Smarty;
	    // assign language to template for caching
	    $smarty->assign('language', $_SESSION['language']);
	    $smarty->caching = false;

	    // set dirs manual
	    $smarty->template_dir = DIR_FS_CATALOG.'templates';
	    $smarty->compile_dir = DIR_FS_CATALOG.'templates_c';
            $smarty->config_dir = DIR_FS_CATALOG.'lang';

	    $smarty->assign('tpl_path', 'templates/'.CURRENT_TEMPLATE.'/');
	    $smarty->assign('logo_path', HTTP_SERVER.DIR_WS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/img/');
		
	    $lang_query = sbf_db_query("select languages_id from languages where directory = '" . $order->info['language'] . "'");
	    $lang = sbf_db_fetch_array($lang_query);
	    $lang=$lang['languages_id'];
	    if (!isset($lang)) $lang=1;
	     
	    $orders_statuses = array ();
	    $orders_status_array = array ();
	    $orders_status_query = sbf_db_query("select orders_status_id, orders_status_name from orders_status where language_id = '".$lang."'");
	    while ($orders_status = sbf_db_fetch_array($orders_status_query)) {
	      $orders_statuses[] = array ('id' => $orders_status['orders_status_id'], 'text' => $orders_status['orders_status_name']);
	      $orders_status_array[$orders_status['orders_status_id']] = $orders_status['orders_status_name'];
	    }

	    $smarty->assign('NAME', $order->customer['name']);
	    $smarty->assign('ORDER_NR', $orders_id_tosync);
	    $smarty->assign('ORDER_LINK', xtc_catalog_href_link("account_history_info.php", 'order_id='.$orders_id_tosync, 'SSL'));
	    $smarty->assign('ORDER_DATE', sbf_date_long($order->info['date_purchased']));
	    $smarty->assign('NOTIFY_COMMENTS', nl2br($notify_comments_mail)); 
	    $smarty->assign('ORDER_STATUS', $orders_status_array[$orders_status_tosync]);

	    $html_mail = $smarty->fetch(CURRENT_TEMPLATE.'/admin/mail/'.$order->info['language'].'/change_order_mail.html');
	    $txt_mail = $smarty->fetch(CURRENT_TEMPLATE.'/admin/mail/'.$order->info['language'].'/change_order_mail.txt');

	    $email_send_status = sbf_php_mail(EMAIL_BILLING_ADDRESS, EMAIL_BILLING_NAME, $order->customer['email_address'], $order->customer['name'], '', EMAIL_BILLING_REPLY_ADDRESS, EMAIL_BILLING_REPLY_ADDRESS_NAME, '', '', EMAIL_BILLING_SUBJECT, $html_mail, $txt_mail); 
	    if (empty ($email_send_status))
		$customer_notified = 1;
	    else
		echo (" <error>" . $email_send_status . "</error>\n");

	    }



	    if (($orders_id_tosync > 0) && ($orders_status_tosync >= 1) && ($orders_status_tosync <= 3)){
		sbf_db_query("UPDATE
						orders
					  SET
					  	orders_status = '".$orders_status_tosync. "'
					  WHERE
					  	orders_id = '" . (int)$orders_id_tosync . "'
					  ");
		sbf_db_query("INSERT INTO
						orders_status_history (orders_id, orders_status_id, date_added, customer_notified, comments)
					  VALUES ('" . (int)$orders_id_tosync . "', '" . $orders_status_tosync . "',
					  		now(), '" . $customer_notified . "', '" . $notify_comments  . "')");
	    }
	}


		// generate list of all products
		if ($action_getproducts) {
			echo (" <products imagepath=\"" . DIR_WS_CATALOG_INFO_IMAGES . "\">\n");
			
			$products_query = sbf_db_query("SELECT 
 												prod.products_model, prod_desc.products_name, prod_desc.products_description, prod_desc.products_short_description, 
												prod.products_image,	 												
												cat_desc.categories_name, prod.products_price, tax.tax_rate, tax.tax_description
											FROM 
												tax_rates tax
											RIGHT JOIN
												zones_to_geo_zones z2geozones ON (z2geozones.geo_zone_id = tax.tax_zone_id) 
											RIGHT JOIN
												countries ON (countries.countries_id =  z2geozones.zone_country_id) AND (countries.countries_iso_code_2 = '". FAKTURAMA_COUNTRY. "')
											RIGHT JOIN
												products prod ON (prod.products_tax_class_id = tax.tax_class_id)
											RIGHT JOIN
												products_to_categories prod_cat ON (prod_cat.products_id = prod.products_id)
											RIGHT JOIN
												categories_description cat_desc ON (prod_cat.categories_id = cat_desc.categories_id)
											RIGHT JOIN
												products_description prod_desc ON (prod.products_id = prod_desc.products_id) 
											LEFT JOIN
												languages langu ON (langu.languages_id = cat_desc.language_id) AND (langu.languages_id = prod_desc.language_id)
  											WHERE 
  												(langu.code = '". $category_language ."')
										   ");
			while ($products = sbf_db_fetch_array($products_query)) {
				echo ("  <product ");
				echo ("gross=\"". number_format( $products['products_price']* (1+ $products['tax_rate']/100), 2) ."\" " );
				echo ("vatpercent=\"". number_format( $products['tax_rate'], 2) ."\" " );
				echo (">\n");
				echo ("   <model>" . my_encode($products['products_model'])."</model>\n");
				echo ("   <name>" . my_encode($products['products_name'])."</name>\n");
				echo ("   <category>" . my_encode($products['categories_name'])."</category>\n");
				echo ("   <vatname>".my_encode($products['tax_description'])."</vatname>\n");
				echo ("   <short_description>" . my_encode_with_quotes(my_strip_tags ( $products['products_short_description'])) . "</short_description>\n");
				echo ("   <image>".$products['products_image']."</image>\n");
				echo ("  </product>\n\n");

			}
			echo (" </products>\n\n\n\n");
		
		}
		



		// generate list of all orders
		if ($action_getorders){
			$check_orders_query = sbf_db_query("SELECT
													o.orders_id, o.orders_status, ot.text AS order_total
												FROM
													orders o
												LEFT JOIN
													orders_total ot ON (o.orders_id = ot.orders_id)
												WHERE
													ot.class = 'ot_total' 
													AND (o.orders_status = '1' ". $getshipped_condition ."  )
												ORDER BY 
													o.orders_id DESC
												"); 


			echo (" <orders>\n");

			while ($check_orders = sbf_db_fetch_array($check_orders_query)) {

				$oID = $check_orders['orders_id'];
				$order = new order($oID);

				
				if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE) {
					$payment_class = $paymentsynonym[ $order->info['payment_method'] ];
					
				}
				if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE) {
					$payment_class = $order->info['payment_class'];
					$order->info['payment_method'] = $paymentsynonym[ $order->info['payment_class'] ];
				}

				$payment_text = $payment_class;

				if ($payment_class == 'cod') 					$payment_text = 'cod'; 	
				if ($payment_class == 'moneyorder') 			$payment_text = 'prepayment'; 	
				if ($payment_class == 'cc') 					$payment_text = 'creditcard'; 	
				if ($payment_class == 'authorizenet_cc_aim')	$payment_text = 'creditcard'; 	
				if ($payment_class == 'authorizenet_cc_sim') 	$payment_text = 'creditcard'; 	
				if ($payment_class == 'chronopay') 				$payment_text = 'chronopay.com'; 	
				if ($payment_class == 'ipayment_cc') 			$payment_text = 'ipayment.de'; 	
				if ($payment_class == 'nochex') 				$payment_text = 'nochex.com'; 	
				if ($payment_class == 'paypal_direct') 			$payment_text = 'paypal.com'; 	
				if ($payment_class == 'paypal_express') 		$payment_text = 'paypal.com'; 	
				if ($payment_class == 'paypal_standard') 		$payment_text = 'paypal.com'; 	
				if ($payment_class == 'paypal_uk_direct') 		$payment_text = 'paypal.com'; 	
				if ($payment_class == 'paypal_uk_express') 		$payment_text = 'paypal.com'; 	
				if ($payment_class == 'pm2checkout') 			$payment_text = '2checkout.com'; 	
				if ($payment_class == 'psigate') 				$payment_text = 'psigate.com'; 	
				if ($payment_class == 'secpay') 				$payment_text = 'secpay.com'; 	
				if ($payment_class == 'sofortueberweisung_direct') $payment_text = 'payment-networt.com'; 	
				if ($payment_class == 'worldpay_junior') 		$payment_text = 'bsworldpay.com'; 	

				$orders_history_query = sbf_db_query("SELECT
														orders_status_id, date_added, comments
													  FROM
													  	orders_status_history
													  WHERE
													  	orders_id = '" . sbf_db_input($oID) . "'
													  ORDER BY
													  	date_added
													  ");



				// if entry is empty, use entry from customers data or from delivery data
				if (empty ($order->billing['telephone']) && !empty ($order->customer['telephone']))
					$order->billing['telephone'] = $order->customer['telephone'];
				if (empty ($order->billing['telephone']) && !empty ($order->delivery['telephone']))
					$order->billing['telephone'] = $order->delivery['telephone'];

				// if entry is empty, use entry from customers data or from delivery data
				if (empty ($order->billing['email_address']) && !empty ($order->customer['email_address']))
					$order->billing['email_address'] = $order->customer['email_address'];
				if (empty ($order->billing['email_address']) && !empty ($order->delivery['email_address']))
					$order->billing['email_address'] = $order->delivery['email_address'];
					


				echo ("  <order id=\"".$oID."\" date=\"".$order->info['date_purchased']."\" ");


				if ($order->info['orders_status'] == 1) $order_status_text = "pending";
				if ($order->info['orders_status'] == 2) $order_status_text = "processing";
				if ($order->info['orders_status'] == 3) $order_status_text = "shipped";

				$total = 0.0;
				if (preg_match("/[0-9]+\.[0-9]+/", str_replace(",",".",strip_tags($check_orders['order_total']) ),$matches))
					$total = $matches[0];

				echo ("currency=\"".$order->info['currency']."\" ");
				echo ("currency_value=\"".$order->info['currency_value']."\" ");
				echo ("status=\"". my_encode($order_status_text). "\" ");
				echo (">\n");

				//echo ('    <cc_type>'.$order->info['cc_type'].'</cc_type>'."\n");
				//echo ('    <cc_owner>'.$order->info['cc_owner'].'</cc_owner>'."\n");
				//echo ('    <cc_number>'.$order->info['cc_number'].'</cc_number>'."\n");
				//echo ('    <cc_expires>'.$order->info['cc_expires'].'</cc_expires>'."\n");
				//echo ('    <last_modified>'.$order->info['last_modified'].'</last_modified>'."\n");


				echo ("   <contact ");
				echo ("id=\"".my_encode($order->customer['cid'])."\">\n");
				echo ("    <gender>".my_encode($order->billing['gender'])."</gender>\n");
				echo ("    <firstname>".my_encode($order->billing['firstname'])."</firstname>\n");
				echo ("    <lastname>".my_encode($order->billing['lastname'])."</lastname>\n");
				echo ("    <company>".my_encode($order->billing['company'])."</company>\n");
				echo ("    <street>".my_encode($order->billing['street_address'])."</street>\n");
				echo ("    <zip>".my_encode($order->billing['postcode'])."</zip>\n");
				echo ("    <city>".my_encode($order->billing['city'])."</city>\n");
				echo ("    <country>".my_encode($order->billing['country'])."</country>\n");
				echo ("    <delivery_gender>".my_encode($order->delivery['gender'])."</delivery_gender>\n");
				echo ("    <delivery_firstname>".my_encode($order->delivery['firstname'])."</delivery_firstname>\n");
				echo ("    <delivery_lastname>".my_encode($order->delivery['lastname'])."</delivery_lastname>\n");
				echo ("    <delivery_company>".my_encode($order->delivery['company'])."</delivery_company>\n");
				echo ("    <delivery_street>".my_encode($order->delivery['street_address'])."</delivery_street>\n");
				echo ("    <delivery_zip>".my_encode($order->delivery['postcode'])."</delivery_zip>\n");
				echo ("    <delivery_city>".my_encode($order->delivery['city'])."</delivery_city>\n");
				echo ("    <delivery_country>".my_encode($order->delivery['country'])."</delivery_country>\n");
				echo ("    <phone>".my_encode($order->billing['telephone'])."</phone>\n");
				echo ("    <email>".my_encode($order->billing['email_address'])."</email>\n");
				echo ("   </contact>\n");


				while ($orders_history = sbf_db_fetch_array($orders_history_query)) {
					if (strlen(trim($orders_history['comments']))){
						echo ("    <comment date=\"" . $orders_history['date_added'] . "\">");
						echo ( my_encode(nl2br(sbf_db_output($orders_history['comments']))));
						echo ("</comment>\n");
					}
				}


				foreach ($order->products as $product) {
					
					$orders_tax_query = sbf_db_query("SELECT
														tax_rate, tax_description
													  FROM
													  	tax_rates
													  WHERE
													  	tax_class_id = '" . $tax_class . "'
													  ");
					if ($taxs = sbf_db_fetch_array($orders_tax_query)) {
						$shipping_tax = $taxs['tax_rate'];
						$shipping_tax_name = $taxs['tax_description'];
					}

					
					
					
					
					echo ("   <item ");
					echo ("id=\"".my_encode($product['id'])."\" ");
					echo ("quantity=\"".$product['qty']."\" ");
					
					if (FAKTURAMA_WEBSHOP_BASE == OSCOMMERCE)
						echo ("gross=\"".number_format( $product['price'] * (1+ $product['tax']/100), 2) ."\" ");
					if (FAKTURAMA_WEBSHOP_BASE == XTCOMMERCE)
						echo ("gross=\"".number_format( $product['price'], 2) ."\" ");

					echo ("vatpercent=\"". number_format($product['tax'],2) . "\">\n");

					echo ("    <model>");
					if (!empty($product['model']))
						echo (my_encode($product['model']));
					else
						echo (my_encode($product['name']));
					echo ("</model>\n");

					echo ("    <name>".my_encode($product['name'])) . "</name>\n";
					echo ("    <category>".my_encode( $product['category']) ."</category>\n");
					echo ("    <vatname>".my_encode($product['tax_description'])."</vatname>\n");



					// Export the product attributes
	        		if ($product['attributes']){
						$subindex = 0;
	          			foreach ($product['attributes'] as $attribute) {
	            			echo ("    <attribute ");
	            			echo ("prefix=\"". my_encode($product['attributes'][$subindex]['prefix']) ."\" ");
	            			echo ("price=\"". my_encode($product['attributes'][$subindex]['price']) ."\"");
	            			echo (">\n");
	            			echo ("     <option>". my_encode($product['attributes'][$subindex]['option']) ."</option>\n");
	            			echo ("     <value>". my_encode($product['attributes'][$subindex]['value']) ."</value>\n");
	            			echo ("    </attribute>\n");
	            			$subindex ++;
	          			}
	        		}
					
					echo ("   </item>\n");
				}

				$totals_query = sbf_db_query("SELECT
												title, text, class
											  FROM
											  	orders_total
											  WHERE
											  	orders_id = '" . (int)$oID . "'
											  	AND class = 'ot_shipping'
											  ORDER BY
											  	sort_order
											  ");
											  
				$shipping_title = "";
				$shipping_text = "";											  
				if ($totals = sbf_db_fetch_array($totals_query)) {
					$shipping_title = $totals['title'];
					$shipping_text = $totals['text'];
				}

				// delete last character, if it is a ":"
				if (substr($shipping_title, -1, 1) == ':')
					$shipping_title = substr($shipping_title, 0, -1);
				;
				
				if (strrpos ( $shipping_title, '(' ))
					$shipping_title = trim (substr($shipping_title, 0, strrpos ( $shipping_title, '(' )) );

				$shipping_tax = 0.0;
				$shipping_tax_name = "";
				$shipping_class = $shippingssynonym[$shipping_title];
				if (! empty($shipping_class)) {
					;
					$configkey = 'MODULE_SHIPPING_'.strtoupper($shipping_class).'_TAX_CLASS';
					$tax_class = $configuration_array[$configkey];
					$orders_tax_query = sbf_db_query("SELECT
														tax_rate, tax_description
													  FROM
													  	tax_rates
													  WHERE
													  	tax_class_id = '" . $tax_class . "'
													  ");
					if ($taxs = sbf_db_fetch_array($orders_tax_query)) {
						$shipping_tax = $taxs['tax_rate'];
						$shipping_tax_name = $taxs['tax_description'];
					}
				}

				$shipping_value = 0.0;
				if (preg_match("/[0-9]+\.[0-9]+/",str_replace(",",".",$shipping_text),$matches))
					$shipping_value = $matches[0];


				echo ("   <shipping ");
				echo ("gross=\"".number_format( $shipping_value , 2)."\" ");
//				echo ("net=\"" .number_format( $shipping_value / ( 1 + $shipping_tax/100), 2)."\" ");
				echo ("vatpercent=\"". number_format($shipping_tax,2) . "\">\n");
				echo ("    <name>".my_encode($shipping_title)."</name>\n");
				echo ("    <vatname>". my_encode($shipping_tax_name) . "</vatname>\n");
				echo ("   </shipping>\n");
				
				
				
				echo ("   <payment ");
				echo ("type=\"". my_encode($payment_text) ."\" ");
				echo ("total=\"".number_format($total,2)."\">\n");
				echo ("    <name>".my_encode($order->info['payment_method'])."</name>\n");
				echo ("   </payment>\n");
				


				echo ("  </order>\n\n");
			}
			echo (" </orders>\n");
		}	
		else {
//			echo (" <error>no valid action set</error>\n");
		}

	}
	else{
		echo (" <error>invalid username or password</error>\n");
	}    

echo ("</webshopexport>\n");


?>
