<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
// Where is this json going on the android client
$response["api"] = 4;
 
// check for required fields
if (isset($_POST['name']) && isset($_POST['code']) && isset($_POST['price']) && isset($_POST['quantity'])) {
 
    $code = $_POST['code'];
    $name = $_POST['name'];
    $price = $_POST['price'];
    $quantity = $_POST['quantity'];
 
    // include db connect class
    require_once '../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO products(code, name, price, quantity) VALUES('$code', '$name', '$price', '$quantity')");
    $id = mysql_insert_id();
    // check if row inserted or not
    if ($result) {
        $product = array();
        $product["id"] = $id;
        $product["code"] = $code;
        $product["name"] = $name;
        $product["price"] = $price;
        $product["quantity"] = $quantity;
        // successfully inserted into database
        $response["success"] = 1;
        //$response["message"] = "Product successfully created.";
        
        $response["product"] = array();
        array_push($response["product"], $product);
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>