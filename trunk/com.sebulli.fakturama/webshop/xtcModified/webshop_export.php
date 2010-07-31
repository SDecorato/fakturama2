<?php
/*
 *  Web shop export script
 *
 *  Version 0.1.2
 *  Date: 2010-07-31
 *
 *
 *	Fakturama - Free Invoicing Software 
 *  Copyright (C) 2010  Gerd Bartelt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

// define our general functions used application-wide
require(DIR_WS_FUNCTIONS . 'general.php');
require(DIR_WS_FUNCTIONS . 'html_output.php');


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
      									customers_id, customers_name, customers_company, customers_street_address,
      									customers_suburb, customers_city, customers_postcode, customers_state,
      									customers_country, customers_telephone, customers_email_address, customers_address_format_id,
      									delivery_name, delivery_company, delivery_street_address, delivery_suburb, delivery_city,
      									delivery_postcode, delivery_state, delivery_country, delivery_address_format_id,
      									billing_name, billing_company, billing_street_address, billing_suburb, billing_city, billing_postcode,
      									billing_state, billing_country, billing_address_format_id, payment_method,".$order_query_payment_class."
      									cc_type, cc_owner, cc_number, cc_expires, currency, currency_value, date_purchased,
      									orders_status, last_modified
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
                          'last_modified' => $order['last_modified']);

      $this->customer = array(
      						  'id' => $order['customers_id'],
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
$customer_notified = (isset($HTTP_POST_VARS['customer_notified']) ? (int)$HTTP_POST_VARS['customer_notified'] : 0); 
$comments = (isset($HTTP_POST_VARS['comments']) ? $HTTP_POST_VARS['comments'] : '');
$orderstosync = (isset($HTTP_POST_VARS['setstate']) ? $HTTP_POST_VARS['setstate'] : '{}');

$action = "getorders";

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




// update the shop values
foreach ($orderstosync as $ordertosync) {

	list($orders_id_tosync, $orders_status_tosync) = explode("=", trim($ordertosync));

	if ($orders_status_tosync == 'pending')    $orders_status_tosync = 1;
	if ($orders_status_tosync == 'processing') $orders_status_tosync = 2;
	if ($orders_status_tosync == 'shipped')    $orders_status_tosync = 3;

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
					  		now(), '" . $customer_notified . "', '" . $comments  . "')");
	}
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



		// generate list of all products
		if ($action == 'getorders') {
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
		if ($action == 'getorders'){
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
				echo ("id=\"".my_encode($order->customer['id'])."\">\n");
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
			echo (" <error>no valid action set</error>\n");
		}

	}
	else{
		echo (" <error>invalid username or password</error>\n");
	}    

echo ("</webshopexport>\n");


?>
