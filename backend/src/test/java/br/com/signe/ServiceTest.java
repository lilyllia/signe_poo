package br.com.signe;

import br.com.signe.service.InventoryManagement;
import br.com.signe.service.Procedure;
import br.com.signe.service.ProcedureCategory;
import br.com.signe.service.Product;

public class ServiceTest {
    Procedure simpleBlowOut = new Procedure("Escova simples", ProcedureCategory.BLOWOUT, 50.00,
            "Shampoo e escova", 50);

    Procedure hairTreatment = new Procedure("Tratamento capilar", ProcedureCategory.HAIR_TREATMENT, 70.00,
            "Shampoo, máscara de tratamento e condicionador", 30);

    Procedure bleachHair = new Procedure("Descoloração global", ProcedureCategory.CHEMICAL_SERVICES, 500.00,
            "Pó descolorante e oxidante", 360);

    Procedure nailExtension = new Procedure("Extensão de unhas", ProcedureCategory.NAILS, 119.99,
            "Gel construtor e molde", 90);

    Product wellaShampoo = new Product("Shampoo", 165.50, "1 litro","Wella", 20);

    Product haskellHairConditioner = new Product("Condicionador", 55.40, "300ml", "Haskell", 15);

    Product nailExtensionGel = new Product("Gel para extensão de unhas", 99.90, "30g", "Bluwe", 25);

    Product brazilianBlowoutKit = new Product("Kit Progressiva Alisante (2 produtos)", 94.90, "350ml", "Borabella", 10);

    InventoryManagement inventoryManagement = new InventoryManagement();
    //inventoryManagement.addProduct(wellaShampoo);
    //inventoryManagement.addProduct(brazilianBlowoutKit);

    //inventoryManagement
}
