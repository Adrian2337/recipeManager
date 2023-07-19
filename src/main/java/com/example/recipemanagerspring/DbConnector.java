package com.example.recipemanagerspring;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
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
            addCategoryToRecipe(r);
            addProductListToRecipe(r);
            }

        return recipes;
    }

    //helper function adding list of products to existing Recipe object from database
    public void addProductListToRecipe(Recipe r){
        r.setProducts(jdbcTemplate.query("SELECT products.id, products.name," +
                " products.quantityName, recipe_product.quantity FROM products INNER JOIN recipe_product " +
                "ON products.id=recipe_product.productid " +
                "WHERE recipe_product.recipeid= ?", BeanPropertyRowMapper.newInstance(Product.class), r.getId()));
    }

    //helper function adding Category object to already existing Recipe object from database
    public void addCategoryToRecipe(Recipe r){
        int id = r.getId();
          r.setCategory(jdbcTemplate.queryForObject("SELECT categories.id, categories.name FROM categories" +
                          " INNER JOIN recipes ON categories.id=recipes.category WHERE recipes.id= ?",
                  BeanPropertyRowMapper.newInstance(Category.class), id));
    }

    //function to get Recipe with given id
    public Recipe getRecipeWithId(int id){
        Recipe r = jdbcTemplate.queryForObject("SELECT id, name, shortdescription, recipetext FROM recipes WHERE recipes.id= ?",
                BeanPropertyRowMapper.newInstance(Recipe.class), id);
        addProductListToRecipe(r);
        addCategoryToRecipe(r);
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
        jdbcTemplate.update("INSERT INTO products (name, quantityName) VALUES(?, ?)", product.getName(),
                product.getQuantityName());

        return true;
    }
    //TODO prevent dupes
    //updates products values at given id
    public boolean editProduct(int id, Product product){
        jdbcTemplate.update("UPDATE products SET name=?, quantityName=? WHERE id=?",
                product.getName(), product.getQuantityName(), id);
        return true;
    }
    //method to add recipe to DB
        public boolean addRecipe(Recipe recipe){
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO recipes (name, shortdescription, recipetext, category) " +
                "VALUES (?,?,?,?)";
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, recipe.getName());
            preparedStatement.setString(2, recipe.getShortDescription());
            preparedStatement.setString(3, recipe.getRecipeText());
            preparedStatement.setInt(4, recipe.getCategory().getId());
            return preparedStatement;
        }, generatedKeyHolder);
        int id = generatedKeyHolder.getKey().intValue();
        recipe.setId(id);
        addRecipeProductListToDB(recipe);
        return true;
    }

    //helper method for inserting to DB list of products with their quantities for Recipe in DB
    public boolean addRecipeProductListToDB(Recipe recipe){
        if(recipe.getProducts().size()==0) return false;
        else{
            //TODO one insert for better performance
            for (Product product: recipe.getProducts()){
                jdbcTemplate.update("INSERT INTO recipe_product (recipeid, productid, quantity) VALUES (?, ?, ?)"
                        , recipe.getId(), product.getId(), product.getQuantity());
            }
        return true;
        }
    }
    //method to delete Recipe from DB
    public boolean deleteRecipe(int id){
        deleteProductList(id);
        jdbcTemplate.update("DELETE FROM recipes WHERE id=?", id);

        return true;
    }
    //method to edit Recipe at given ID
       public boolean editRecipe(int id, Recipe recipe){
        Recipe oldRecipe = getRecipeWithId(id);
        jdbcTemplate.update("UPDATE recipes SET name=?, shortDescription=?, recipetext=?, category=? WHERE id=?",
                recipe.getName(), recipe.getShortDescription(), recipe.getRecipeText(), recipe.getCategory().getId(), id);
        if(oldRecipe.getProducts().equals(recipe.getProducts())){
            return true;
        } else{
            deleteProductList(recipe.getId());
            addRecipeProductListToDB(recipe);
            return true;
        }
    }
    //deletes list of product belonging to Recipe with given id from DB recipe_product table
    public boolean deleteProductList(int id){
        jdbcTemplate.update("DELETE FROM recipe_product WHERE recipeid=?", id);
        return true;
    }

    //method to get all recipes using certain product
    public List<Recipe> getRecipesContainingProduct(int productId){
        List<Recipe> recipes = new ArrayList<>();
        List<Integer> recipeIds = jdbcTemplate.queryForList("SELECT recipeid FROM recipe_product WHERE productid=?",
                Integer.class, productId);

        for (Integer r: recipeIds){
           recipes.add(getRecipeWithId(r));
       }

        return recipes;
    }

}
