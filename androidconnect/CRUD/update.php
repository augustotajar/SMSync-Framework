<?php
 
/*
 * Following code will update a product information
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
// Where is this json going on the android client
$response["api"] = 3;
 
// check for required fields
if (isset($_POST['id']) && isset($_POST['code']) && isset($_POST['name']) && isset($_POST['price']) && isset($_POST['quantity'])) {
 
    $id = $_POST['id'];
    $code = $_POST['code'];
    $name = $_POST['name'];
    $price = $_POST['price'];
    $quantity = $_POST['quantity'];
 
    // include db connect class
    require_once '../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched pid
    $result = mysql_query("UPDATE products SET name = '$name', code = '$code', price = '$price', quantity = '$quantity' WHERE id = $id");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $product = array();
        $product["id"] = $id;
        $product["code"] = $code;
        $product["name"] = $name;
        $product["price"] = $price;
        $product["quantity"] = $quantity;
            
        $response["product"] = array();
        array_push($response["product"], $product);
        
        $response["success"] = 1;
        //$response["message"] = "Product successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>