/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import dao.LotEnStockDAO;
import entities.EnStock;
import entities.LigneVnt;
import entities.Produit;
import entities.Unite;
import entities.Vente;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alilo
 */
public class ConcurrentSales {

    public static void main(String[] args) {
        int count = 25;
        if (args.length > 0) {
            try {
                count = Integer.valueOf(args[0]);
            } catch (NumberFormatException ex) {
                System.err.println(ex.getMessage());
            }
        }
        System.out.println("Sales count: " + count);

        final ConcurrentSales sales = new ConcurrentSales();
        List<Vente> s1 = sales.create(count);
        List<Vente> s2 = sales.create(count);
        List<Vente> s3 = sales.create(count);
        List<Vente> s4 = sales.create(count);

        sales.validate(s1).start();
        sales.validate(s2).start();
        sales.validate(s3).start();
        sales.validate(s4).start();

    }

    public Thread validate(List<Vente> sales) {
        return new Thread() {
            @Override
            public void run() {
                sales.forEach(v -> {
                    v.validate();
                    System.out.println(this.getName() + "/validated: " + v);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                    }
                });
            }
        };
    }

    synchronized public List<Vente> create(int count) {
        Thread th = Thread.currentThread();
        List<Vente> sales = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Vente v = new Vente(0);
            v.insert();
            // item 1
            EnStock item1 = LotEnStockDAO.getInstance().getObjectByID(2527);
            addSaleItem(v, item1);
            // item 2
            EnStock item2 = LotEnStockDAO.getInstance().getObjectByID(2528);
            addSaleItem(v, item2);
            sales.add(v);
            System.out.println(th.getName() + "/created: " + v);
        }
        return sales;
    }

    private void addSaleItem(Vente vnt, EnStock lot) {
        // create new 'LigneVente'.
        LigneVnt newLVnt = new LigneVnt(0);
        // set the 'Vente'
        newLVnt.setVente(vnt);
        // set the 'Lot'
        newLVnt.setEnStock(lot);
        // set qte & price
        BigDecimal puVnt = lot.getPuVntDt();
        newLVnt.setPuVnt(puVnt);
        newLVnt.setPuAch(lot.getPuAch());
        newLVnt.setQte(1);
        // set the 'Unite.Vente'
        Produit prodVend = lot.getProduit();
        Unite unite = prodVend.getUnite();
        newLVnt.setUniteVnt(unite);
        newLVnt.setQteUnitair(unite.getQte());
        double total = puVnt.doubleValue() * unite.getQte();
        newLVnt.setTotalLvnt(new BigDecimal(total).setScale(2, RoundingMode.HALF_UP));
        newLVnt.insert();
    }

}
