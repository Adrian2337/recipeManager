package com.example.recipemanagerspring;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DbConnector {

    final
    JdbcTemplate jdbcTemplate;

    public DbConnector(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //function listing all Products in database
    public List<Product> getAllProducts(){
        return jdbcTemplate.query("SELECT * FROM products", BeanPropertyRowMapper.newInstance(Product.class));
    }

    //function listing all Categories in database
    public List<Category> getAllCategories(){
        return jdbcTemplate.query("SELECT * FROM categories", BeanPropertyRowMapper.newInstance(Category.class));
    }

    //function listing all Recipes in database
    public List<Recipe> getAllRecipes(){
        List<Recipe> recipes = jdbcTemplate.query(
                "SELECT id, name, shortdescription, recipetext FROM recipes", BeanPropertyRowMapper.newInstance((Recipe.class)));

        for(Recipe r : recipes){
            addCategory(r);
            addProductList(r);
            }

        return recipes;
    }

    //helper function adding list of products to existing Recipe object from database
    public void addProductList(Recipe r){
        r.setProducts(jdbcTemplate.query("SELECT products.id, products.name," +
                " products.quantityName, recipe_product.quantity FROM products INNER JOIN recipe_product " +
                "ON products.id=recipe_product.productid " +
                "WHERE recipe_product.recipeid= ?", BeanPropertyRowMapper.newInstance(Product.class), r.getId()));
    }

    //helper function adding Category object to already existing Recipe object from database
    public void addCategory(Recipe r){
        int id = r.getId();
          r.setCategory(jdbcTemplate.queryForObject("SELECT categories.id, categories.name FROM categories" +
                          " INNER JOIN recipes ON categories.id=recipes.category WHERE recipes.id= ?",
                  BeanPropertyRowMapper.newInstance(Category.class), id));
    }

    //function to get Recipe with given id
    public Recipe getRecipeWithId(int id){
        Recipe r = jdbcTemplate.queryForObject("SELECT id, name, shortdescription, recipetext FROM recipes WHERE recipes.id= ?",
                BeanPropertyRowMapper.newInstance(Recipe.class), id);
        addProductList(r);
        addCategory(r);
        System.out.println(r.getRecipeText());
        return r;
    }

    //function to get Product with given id
    public Product getProductWithId(int id){
        return jdbcTemplate.queryForObject("SELECT * FROM products WHERE products.id=?",
                BeanPropertyRowMapper.newInstance(Product.class), id);
    }

    //function to get Category with given id
    public Category getCategoryWithId(int id){
        return jdbcTemplate.queryForObject("SELECT * FROM categories where categories.id=?",
                BeanPropertyRowMapper.newInstance(Category.class), id);
    }

    //method to add new product to database
    //TODO prevent dupes
    public boolean addProduct(Product product){
        jdbcTemplate.update("INSERT INTO products (products.name, products.quantityName) VALUES(?, ?)", product.getName(),
                product.getQuantityName());

        return true;
    }

    //updates products values at given id
    public boolean editProduct(int id, Product product){
        jdbcTemplate.update("UPDATE products SET products.name=?, products.quantityName=? WHERE products.id=?",
                product.getName(), product.getQuantityName(), id);
        return true;
    }
}
