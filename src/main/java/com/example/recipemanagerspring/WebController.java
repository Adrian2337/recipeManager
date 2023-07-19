package com.example.recipemanagerspring;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WebController {

    final
    DbConnector dbConnector;

    public WebController(DbConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts(){
        return dbConnector.getAllProducts();
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories(){
        return dbConnector.getAllCategories();
    }

    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes(){
        return dbConnector.getAllRecipes();
    }

    @GetMapping("/recipe")
    public Recipe getRecipeWithId(@RequestParam String id){
        return dbConnector.getRecipeWithId(Integer.parseInt(id));
         }

    @GetMapping("/product")
    public Product getProductWithId(@RequestParam String id){
        return dbConnector.getProductWithId(Integer.parseInt(id));
    }

    @PostMapping("/products/add")
    public boolean addProduct(@RequestBody Product product){
        return dbConnector.addProduct(product);
    }

    @PutMapping("/product")
    public boolean editProduct(@RequestParam String id, @RequestBody Product product){
        return dbConnector.editProduct(Integer.parseInt(id), product);
    }

    @PostMapping("/recipe/add")
    public boolean addRecipe(@RequestBody Recipe recipe){
        return dbConnector.addRecipe(recipe);
    }

    @DeleteMapping("/recipe")
    public boolean deleteRecipe(@RequestParam String id){
        return dbConnector.deleteRecipe(Integer.parseInt(id));
    }

    @PutMapping("/recipe")
    public  boolean editRecipe(@RequestParam String id, @RequestBody Recipe recipe){ return dbConnector.editRecipe(Integer.parseInt(id), recipe);}
}
