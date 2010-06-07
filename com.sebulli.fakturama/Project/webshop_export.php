<?php
/*
  Copyright (c) 2010 Gerd Bartelt
  Version 1.0.0
  Date: 2010/04/17

  Released under the GNU General Public License
 */

// Set the level of error reporting
error_reporting(E_ALL & ~E_NOTICE);

// check support for register_globals
if (function_exists('ini_get') && (ini_get('register_globals') == false) && (PHP_VERSION < 4.3) ) {
	exit('Server Requirement Error: register_globals is disabled in your PHP configuration. This can be enabled in your php.ini configuration file or in the .htaccess file in your catalog directory. Please use PHP 4.3+ if register_globals cannot be enabled on the server.');
}

// Set the local configuration parameters - mainly for developers
if (file_exists('includes/local/configure.php')) include('includes/local/configure.php');

// Include application configuration parameters
require('includes/configure.php');

// Define the project version
define('PROJECT_VERSION', 'osCommerce Online Merchant v2.2 RC2a');

// some code to solve compatibility issues
require(DIR_WS_FUNCTIONS . 'compatibility.php');

// set php_self in the local scope
$PHP_SELF = (isset($HTTP_SERVER_VARS['PHP_SELF']) ? $HTTP_SERVER_VARS['PHP_SELF'] : $HTTP_SERVER_VARS['SCRIPT_NAME']);


// include the list of project filenames
require(DIR_WS_INCLUDES . 'filenames.php');

// include the list of project database tables
require(DIR_WS_INCLUDES . 'database_tables.php');

// Define how do we update currency exchange rates
// Possible values are 'oanda' 'xe' or ''
define('CURRENCY_SERVER_PRIMARY', 'oanda');
define('CURRENCY_SERVER_BACKUP', 'xe');

// include the database functions
require(DIR_WS_FUNCTIONS . 'database.php');

// make a connection to the database... now
tep_db_connect() or die('Unable to connect to database server!');

// set application wide parameters
$configuration_query = tep_db_query('select configuration_key as cfgKey, configuration_value as cfgValue from ' . TABLE_CONFIGURATION);
while ($configuration = tep_db_fetch_array($configuration_query)) {
	$configuration_array[$configuration['cfgKey']] = $configuration['cfgValue'];
	define($configuration['cfgKey'], $configuration['cfgValue']);
}


// define our general functions used application-wide
require(DIR_WS_FUNCTIONS . 'general.php');
require(DIR_WS_FUNCTIONS . 'html_output.php');


require('includes/functions/password_funcs.php');

function my_encode($s) {
	$s = str_replace("\"", "&quot;", $s);
	return utf8_encode($s);
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
      $order_query = tep_db_query("select customers_id, customers_name, customers_company, customers_street_address, customers_suburb, customers_city, customers_postcode, customers_state, customers_country, customers_telephone, customers_email_address, customers_address_format_id, delivery_name, delivery_company, delivery_street_address, delivery_suburb, delivery_city, delivery_postcode, delivery_state, delivery_country, delivery_address_format_id, billing_name, billing_company, billing_street_address, billing_suburb, billing_city, billing_postcode, billing_state, billing_country, billing_address_format_id, payment_method, cc_type, cc_owner, cc_number, cc_expires, currency, currency_value, date_purchased, orders_status, last_modified from " . TABLE_ORDERS . " where orders_id = '" . (int)$order_id . "'");
      $order = tep_db_fetch_array($order_query);

      $totals_query = tep_db_query("select title, text from " . TABLE_ORDERS_TOTAL . " where orders_id = '" . (int)$order_id . "' order by sort_order");
      while ($totals = tep_db_fetch_array($totals_query)) {
        $this->totals[] = array('title' => $totals['title'],
                                'text' => $totals['text']);
      }

      $this->info = array('currency' => $order['currency'],
                          'currency_value' => $order['currency_value'],
                          'payment_method' => $order['payment_method'],
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


      $orders_address_query = tep_db_query("select customers_id, entry_gender, entry_firstname, entry_lastname from " . TABLE_ADDRESS_BOOK . " where customers_id = '" . (int)$customers_id . "'");

     while ($orders_address = tep_db_fetch_array($orders_address_query)) {
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
      
     $orders_products_query = tep_db_query("select tax.tax_description, ordprod.orders_products_id, ordprod.products_name, ordprod.products_model, ordprod.products_price, ordprod.products_tax, ordprod.products_quantity, ordprod.final_price from " . TABLE_TAX_RATES . " tax left join " . TABLE_PRODUCTS . " prod on (prod.products_tax_class_id = tax.tax_rates_id ) left join " . TABLE_ORDERS_PRODUCTS . " ordprod on (prod.products_id = ordprod.products_id) where ordprod.orders_id = '" . (int)$order_id . "'");
      
      
      while ($orders_products = tep_db_fetch_array($orders_products_query)) {
        $this->products[$index] = array(
								        'id' => $orders_products['orders_products_id'],
								        'qty' => $orders_products['products_quantity'],
                                        'name' => $orders_products['products_name'],
                                        'model' => $orders_products['products_model'],
                                        'tax' => $orders_products['products_tax'],
                                        'tax_description' => $orders_products['tax_description'],
                                        'price' => $orders_products['products_price'],
                                        'final_price' => $orders_products['final_price']);

        $subindex = 0;
        $attributes_query = tep_db_query("select products_options, products_options_values, options_values_price, price_prefix from " . TABLE_ORDERS_PRODUCTS_ATTRIBUTES . " where orders_id = '" . (int)$order_id . "' and orders_products_id = '" . (int)$orders_products['orders_products_id'] . "'");
        if (tep_db_num_rows($attributes_query)) {
          while ($attributes = tep_db_fetch_array($attributes_query)) {
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
if (defined('MODULE_PAYMENT_INSTALLED') && tep_not_null(MODULE_PAYMENT_INSTALLED)) {
	$modules_payment = explode(';', MODULE_PAYMENT_INSTALLED);

	$include_modules_payment = array();

	if ( (tep_not_null($module)) && (in_array($module . '.' . substr($PHP_SELF, (strrpos($PHP_SELF, '.')+1)), $modules_payment)) ) {
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
if (defined('MODULE_SHIPPING_INSTALLED') && tep_not_null(MODULE_SHIPPING_INSTALLED)) {
	$modules_shipping = explode(';', MODULE_SHIPPING_INSTALLED);

	$include_modules_shipping = array();

	if ( (tep_not_null($module)) && (in_array($module . '.' . substr($PHP_SELF, (strrpos($PHP_SELF, '.')+1)), $modules_shipping)) ) {
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
$languages_query = tep_db_query("select directory from " . TABLE_LANGUAGES );
while ($languages = tep_db_fetch_array($languages_query)) {
	for ($i=0, $n=sizeof($include_modules_payment); $i<$n; $i++) {
		$filename = "../" . DIR_WS_LANGUAGES . $languages[directory] . '/modules/payment/' . $include_modules_payment[$i]['file'];	
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
				if ($paymenttext)
					$paymentsynonym[$paymenttext] = $include_modules_payment[$i]['class'];
			}
		} 
		fclose($paymentfile); 
	}
}

// search all shippings for the shipping method
$languages_query = tep_db_query("select directory from " . TABLE_LANGUAGES );
while ($languages = tep_db_fetch_array($languages_query)) {
	for ($i=0, $n=sizeof($include_modules_shipping); $i<$n; $i++) {
		$filename = "../" . DIR_WS_LANGUAGES . $languages[directory] . '/modules/shipping/' . $include_modules_shipping[$i]['file'];	
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


// parse POST parameters
$getshipped = (isset($HTTP_POST_VARS['getshipped']) ? $HTTP_POST_VARS['getshipped'] : '');
$action = (isset($HTTP_POST_VARS['action']) ? $HTTP_POST_VARS['action'] : '');
$customer_notified = (isset($HTTP_POST_VARS['customer_notified']) ? (int)$HTTP_POST_VARS['customer_notified'] : 0); 
$comments = (isset($HTTP_POST_VARS['comments']) ? $HTTP_POST_VARS['comments'] : '');
$orderstosync = (isset($HTTP_POST_VARS['setstate']) ? $HTTP_POST_VARS['setstate'] : '{}');

$orderstosync = substr($orderstosync, 0, -1);
$orderstosync = substr($orderstosync, 1);
$orderstosync = explode(",", $orderstosync);

$username = tep_db_prepare_input($HTTP_POST_VARS['username']);
$password = tep_db_prepare_input($HTTP_POST_VARS['password']);

$check_query = tep_db_query("select id, user_name, user_password from " . TABLE_ADMINISTRATORS . " where user_name = '" . tep_db_input($username) . "'");


// generate header of response
echo ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
echo ("<webshopexport version=\"1.0\" >\n");
echo ("<webshop shop=\"oscommerce\" version=\"".PROJECT_VERSION."\"></webshop>\n");



// update the shop values
foreach ($orderstosync as $ordertosync) {

	list($orders_id_tosync, $orders_status_tosync) = explode("=", trim($ordertosync));

	if ($orders_status_tosync == 'pending')    $orders_status_tosync = 1;
	if ($orders_status_tosync == 'processing') $orders_status_tosync = 2;
	if ($orders_status_tosync == 'shipped')    $orders_status_tosync = 3;

	if (($orders_id_tosync > 0) && ($orders_status_tosync >= 1) && ($orders_status_tosync <= 3)){
		tep_db_query("update " . TABLE_ORDERS . " set orders_status = '".$orders_status_tosync. "' where orders_id = '" . (int)$orders_id_tosync . "'");
		tep_db_query("insert into " . TABLE_ORDERS_STATUS_HISTORY . " (orders_id, orders_status_id, date_added, customer_notified, comments) values ('" . (int)$orders_id_tosync . "', '" . $orders_status_tosync . "', now(), '" . $customer_notified . "', '" . $comments  . "')");
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



if (tep_db_num_rows($check_query) == 1) {
	$check = tep_db_fetch_array($check_query);
	if (tep_validate_password($password, $check['user_password'])) {

		//password ok


		// generate response
		if ($action == 'getorders'){
			$check_orders_query = tep_db_query("select o.orders_id, o.orders_status, ot.text as order_total from " . TABLE_ORDERS . " o left join " . TABLE_ORDERS_TOTAL . " ot on (o.orders_id = ot.orders_id) where ot.class = 'ot_total' and (o.orders_status = '1' ". $getshipped_condition ."  ) ORDER BY o.orders_id DESC"); 


			echo (" <orders>\n");

			while ($check_orders = tep_db_fetch_array($check_orders_query)) {

				$oID = $check_orders['orders_id'];
				$order = new order($oID);

				$payment_class = $paymentsynonym[ $order->info['payment_method'] ];
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

				$orders_history_query = tep_db_query("select orders_status_id, date_added, comments from " . TABLE_ORDERS_STATUS_HISTORY . " where orders_id = '" . tep_db_input($oID) . "' order by date_added");



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
				if (preg_match("/[0-9]+\.[0-9]+/",strip_tags($check_orders['order_total']),$matches))
					$total = $matches[0];

				echo ("currency=\"".$order->info['currency']."\" ");
				echo ("status=\"". my_encode($order_status_text). "\" >\n");

				//echo ('    <currency_value>'.$order->info['currency_value'].'</currency_value>'."\n");
				//echo ('    <cc_type>'.$order->info['cc_type'].'</cc_type>'."\n");
				//echo ('    <cc_owner>'.$order->info['cc_owner'].'</cc_owner>'."\n");
				//echo ('    <cc_number>'.$order->info['cc_number'].'</cc_number>'."\n");
				//echo ('    <cc_expires>'.$order->info['cc_expires'].'</cc_expires>'."\n");
				//echo ('    <last_modified>'.$order->info['last_modified'].'</last_modified>'."\n");


				echo ("    <contact ");
				echo ("id=\"".my_encode($order->customer['id'])."\" ");
				echo ("gender=\"".my_encode($order->billing['gender'])."\" ");
				echo ("firstname=\"".my_encode($order->billing['firstname'])."\" ");
				echo ("lastname=\"".my_encode($order->billing['lastname'])."\" ");
				echo ("company=\"".my_encode($order->billing['company'])."\" ");
				echo ("street=\"".my_encode($order->billing['street_address'])."\" ");
				echo ("zip=\"".my_encode($order->billing['postcode'])."\" ");
				echo ("city=\"".my_encode($order->billing['city'])."\" ");
				echo ("country=\"".my_encode($order->billing['country'])."\" ");
				echo ("delivery_gender=\"".my_encode($order->delivery['gender'])."\" ");
				echo ("delivery_firstname=\"".my_encode($order->delivery['firstname'])."\" ");
				echo ("delivery_lastname=\"".my_encode($order->delivery['lastname'])."\" ");
				echo ("delivery_company=\"".my_encode($order->delivery['company'])."\" ");
				echo ("delivery_street=\"".my_encode($order->delivery['street_address'])."\" ");
				echo ("delivery_zip=\"".my_encode($order->delivery['postcode'])."\" ");
				echo ("delivery_city=\"".my_encode($order->delivery['city'])."\" ");
				echo ("delivery_country=\"".my_encode($order->delivery['country'])."\" ");
				echo ("phone=\"".my_encode($order->billing['telephone'])."\" ");
				echo ("email=\"".my_encode($order->billing['email_address'])."\" ");
				echo ("></contact>\n");


				while ($orders_history = tep_db_fetch_array($orders_history_query)) {
					if (strlen(trim($orders_history['comments']))){
						echo ("    <comment date=\"" . $orders_history['date_added'] . "\">");
						echo ( my_encode(nl2br(tep_db_output($orders_history['comments']))));
						echo ("</comment>\n");
					}
				}


				foreach ($order->products as $product) {
					
					$orders_tax_query = tep_db_query("select tax_rate, tax_description from " . TABLE_TAX_RATES . " where tax_class_id = '" . $tax_class . "'");
					if ($taxs = tep_db_fetch_array($orders_tax_query)) {
						$shipping_tax = $taxs['tax_rate'];
						$shipping_tax_name = $taxs['tax_description'];
					}

					
					
					
					
					echo ("    <item ");
					echo ("id=\"".my_encode($product['id'])."\" ");
					echo ("quantity=\"".$product['qty']."\" ");
					echo ("name=\"".my_encode($product['model'])."\" ");
					echo ("description=\"".my_encode($product['name']));
					/*
	        		if ($product['attributes']){
	          			foreach ($product['attributes'] as $attribute) {
	            			echo ('        <attribute>'."\n");
	            			echo ('          <option>'.$product['attributes'][$attribute].'</option>'."\n");
	            			echo ('          <value>'.$product['attributes'][$attribute].'</value>'."\n");
	            			echo ('          <prefix>'.$product['attributes'][$attribute].'</prefix>'."\n");
	            			echo ('          <price>'.$product['attributes'][$attribute].'</price>'."\n");
	            			echo ('        </attribute>'."\n");
	          			}
	        		}
					*/
					echo ("\" ");
					echo ("totalnet=\"".number_format( $product['qty'] * $product['final_price'], 2) ."\" ");
					echo ("totalgross=\"".number_format( $product['qty'] * $product['final_price'] * (1+$product['tax']/100), 2)."\" ");
					echo ("vatpercent=\"". number_format($product['tax'],2) . "\" ");
					echo ("vatname=\"".my_encode($product['tax_description'])."\" ");
					echo (">");

					echo ("</item>\n");
				}

				$totals_query = tep_db_query("select title, text, class from " . TABLE_ORDERS_TOTAL . " where orders_id = '" . (int)$oID . "' order by sort_order");
				while ($totals = tep_db_fetch_array($totals_query)) {
					$totals_a[] = array('title' => $totals['title'],
							'text' => $totals['text'],
							'class' => $totals['class']);
				}

				for ($i = 0, $n = sizeof($totals_a); $i < $n; $i++) {
					if ($totals_a[$i]['class'] == 'ot_shipping'){
						$shipping_title = $totals_a[$i]['title'];
						$shipping_text = $totals_a[$i]['text'];
					}
				}

				// delete last character, if it is a ":"
				if (substr($shipping_title, -1, 1) == ':')
					$shipping_title = substr($shipping_title, 0, -1);
				;
				
				if (strrpos ( $shipping_title, '(' ))
					$shipping_title = trim (substr($shipping_title, 0, strrpos ( $shipping_title, '(' )) );

				$shipping_tax = 0.0;
				$shipping_class = $shippingssynonym[$shipping_title];
				if (! empty($shipping_class)) {
					;
					$configkey = 'MODULE_SHIPPING_'.strtoupper($shipping_class).'_TAX_CLASS';
					$tax_class = $configuration_array[$configkey];
					$orders_tax_query = tep_db_query("select tax_rate, tax_description from " . TABLE_TAX_RATES . " where tax_class_id = '" . $tax_class . "'");
					if ($taxs = tep_db_fetch_array($orders_tax_query)) {
						$shipping_tax = $taxs['tax_rate'];
						$shipping_tax_name = $taxs['tax_description'];
					}
				}

				if (preg_match("/[0-9]+\.[0-9]+/",$shipping_text,$matches))
					$shipping_value = $matches[0];


				echo ("    <shipping ");
				echo ("name=\"".my_encode($shipping_title)."\" ");
				echo ("net=\"" .number_format( $shipping_value / ( 1 + $shipping_tax/100), 2)."\" ");
				echo ("gross=\"".number_format( $shipping_value , 2)."\" ");
				echo ("vatpercent=\"". number_format($shipping_tax,2) . "\" ");
				echo ("vatname=\"". $shipping_tax_name . "\" ");
				echo ("></shipping>\n");
				
				echo ("    <payment ");
				echo ("id=\"". my_encode($payment_text) ."\" ");
				echo ("name=\"". my_encode($order->info['payment_method']) ."\" ");
				echo ("total=\"".number_format($total,2)."\" ");
				echo ("></payment>\n");
				


				echo ("  </order>\n");
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
}
else
	echo (" <error>enter unsername and password</error>\n");

echo ("</webshopexport>\n");


?>
