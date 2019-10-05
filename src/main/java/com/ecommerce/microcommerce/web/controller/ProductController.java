package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.ecommerce.microcommerce.web.exceptions.ProduitListIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


@Api( description="API pour es opérations CRUD sur les produits.")

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;
    
  //Récupérer la liste des produits
    @RequestMapping(value = "/AdminProduits", method = RequestMethod.GET)
    public ResponseEntity<DataResponse> calculerMargeProduit() {
    	List<Product> listProduits = productDao.findAll();
    	List<Object> listProduitMarge = new ArrayList<Object>(listProduits.size()) ;
    
    	if(listProduits != null) {
	    	DataResponse response = new DataResponse(); 	
	    	listProduits.forEach((product) ->{ 
	    		listProduitMarge.add( product.toString() + ": " +Integer.toString(product.getPrix() - product.getPrixAchat()));
	    		});
	    	response.setSuccess(true);
	        response.setCount(listProduits.size());
	        response.setProducts(listProduitMarge);
	        return new ResponseEntity<>(response,HttpStatus.OK);
	    
    	}
        throw new ProduitListIntrouvableException("Produits introuvable");
    //
    }
    
    @RequestMapping(value = "/Produits/trier", method = RequestMethod.GET)
    public ResponseEntity<DataResponse> trierProduitsParOrdreAlphabetique ()  {
    	   	
    	List<Product> results =  productDao.findByOrderByNomAsc();
    	if(results != null) {
	        DataResponse response = new DataResponse();
	        
	        response.setSuccess(true);
	        response.setCount(results.size());
	        response.setProducts(results);
	        return new ResponseEntity<>(response,HttpStatus.OK);
    	}
    	throw new ProduitListIntrouvableException("Produits introuvable");
    }

    //Récupérer la liste des produitsZ
    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public ResponseEntity<DataResponse> listeProduits() {

        List<Product> produits = productDao.findAll();

//        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
//        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
//        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
//        produitsFiltres.setFilters(listDeNosFiltres);
        
        if(produits != null) {
        	 DataResponse response = new DataResponse();
 	        response.setSuccess(true);
 	        response.setCount(produits.size());
 	        response.setProducts(produits);
 	        return new ResponseEntity<>(response,HttpStatus.OK);
        }
        throw new ProduitListIntrouvableException("Produits introuvable");
    }


    //Récupérer un produit par son Id
    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")
    public Product afficherUnProduit(@PathVariable int id) {

        Product produit = productDao.findById(id);

        if(produit==null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");

        return produit;
    }




    //ajouter un produit
    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) throws Exception {

    	if(product.getPrix() <= 0) throw new ProduitGratuitException("Le produit avec l'id " + product.getId() + " à un prix d'achat non valide. Il doit être positif.");
        
    	Product productAdded =  productDao.save(product);

        if (productAdded == null) {
        	throw new Exception("Le produit avec l'id " + product.getId() + " n'à pas pu être enregistré.");
            //return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable int id) {

        productDao.delete(id);
    }

    @PutMapping (value = "/Produits")
    public void updateProduit(@RequestBody Product product) {

        productDao.save(product);
    }


    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    public List<Product>  testeDeRequetes(@PathVariable int prix) {

        return productDao.chercherUnProduitCher(400);
    }



}
