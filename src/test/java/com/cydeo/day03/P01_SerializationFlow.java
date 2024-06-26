package com.cydeo.day03;

import com.cydeo.pojo.CategoryPOST;
import com.cydeo.utility.FakeStoreTestBase;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class P01_SerializationFlow extends FakeStoreTestBase {

/*
    Given accept header is application json
    And content type is application json
    And requestBody field and values are
        |name                 |image  |
        |My Category          |url  |
    When I send POST request /categories endpoint
    Then status code should be 201
    And response body should have posted product information
    And id field should exist
 */

    static int id;
    static CategoryPOST category ;

    @Order(1)
    @Test
    public void post() {

    /*
        - String   -- X
        - Java --> MAP / OBJECT ( POJO )
        - JSON File

     */

        category=new CategoryPOST();
        category.setName("Nice Product");
        category.setImage("https://t4.ftcdn.net/jpg/00/81/38/59/360_F_81385977_wNaDMtgrIj5uU5QEQLcC9UNzkJc57xbu.jpg");

        /*
        Map<String,String> catMap=new HashMap<>();
        catMap.put("name","Nice Product");
        catMap.put("image","https://t4.ftcdn.net/jpg/00/81/38/59/360_F_81385977_wNaDMtgrIj5uU5QEQLcC9UNzkJc57xbu.jpg");
        */

        log.info("POST /categories end point is working");
        log.info("Category object is created with following data");
        log.info(category);

        Response response = given()
                .accept(ContentType.JSON)
               .contentType(ContentType.JSON)
                .body(category).
                when().post("/categories");





        JsonPath jp = response.jsonPath();


        // ASSERTION FOR NAME
        String name = jp.getString("name");
        assertEquals(category.getName(),name);

        // ASSERTION FOR URL
        String image = jp.getString("image");
        assertEquals(category.getImage(),image);

        // ID
        id = jp.getInt("id");

        if(response.statusCode()==201){
            log.info("Category is generated with following id "+id);
        }else
        {
            log.error("It fails and status code is "+response.statusCode());
        }

    }
    @Order(2)
    @Test
    public void getCategory() {

        log.info("-- id is retrived from previous execution to make sure category is generated"+id);

        given().accept(ContentType.JSON)
                .pathParam("id",id).
        when().get("/categories/{id}").
        then().statusCode(200)
                .body("name",is(category.getName()));


        log.info("-- GET /categories/{id} endpoint worked and name is verified");


    }

    @Order(3)
    @Test
    public void delete() {
        log.info("-- id is retrived from previous execution to delete this product"+id);

        Response response = given().accept(ContentType.JSON)
                .pathParam("id", id).
                when().delete("/categories/{id}")
                .then()
                .statusCode(200)
                .extract().response();


        String responseTrue = response.asString();
        System.out.println(responseTrue);
        assertEquals("true",responseTrue);


        log.info("-- DELETE /categories/{id} endpoint worked with following id"+id);


    }
    @Order(4)
    @Test
    public void getCategory2() {


        given().accept(ContentType.JSON)
                .pathParam("id",id).
                when().get("/categories/{id}").
                then().statusCode(400)
                .body("name",is("EntityNotFoundError"));


        log.info("-- GET /categories/{id} endpoint worked and verified "+id+" is deleted");


    }
}
