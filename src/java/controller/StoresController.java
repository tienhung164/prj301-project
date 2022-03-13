/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.stores;
import entity.tiltes;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.DAOstores;

/**
 *
 * @author pallgree
 */
@WebServlet(name = "StoresController", urlPatterns = {"/StoresController"})
public class StoresController extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DAOstores dao = new DAOstores();
        String service = request.getParameter("go");
        HttpSession session = request.getSession();
        response.setContentType("text/html;charset=UTF-8");
        if (service == null) {
            service = "listAllStores";
        }
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            if (service.equals("insertStore")){
                String stor_id = request.getParameter("stor_id");
                String stor_name = request.getParameter("stor_name");
                String stor_address = request.getParameter("stor_address");
                String city = request.getParameter("city");
                String state = request.getParameter("state");
                String zip = request.getParameter("zip");
                if (stor_id == null || stor_id.equals("")) {
                    out.print("<h2>Stores ID is not null</h2>");
                    return;
                }
                stores sto = new stores(stor_id, stor_name, stor_address, city, state, zip);
                dao.addStores1(sto);
                response.sendRedirect("StoresController");

            }
            if (service.equals("listAllStores")) {
                
                String isLogin = (String) session.getAttribute("user");
                if (isLogin == null) {
                    response.sendRedirect("admin");
                }else{
                
                Vector<stores> vector = dao.viewAll();
                request.setAttribute("list", vector);

                 request.getRequestDispatcher("template/admin/store.jsp").forward(request, response);
                }
            }
            
             if(service.equals("storedetail")){
               
                String stor_id = request.getParameter("storID");
                String stor_name=request.getParameter("storname");              
                String sql = "select  s.ord_num, t.title, s.qty, t.price, s.qty*t.price as Total\n"
                        + "from sales s\n"
                        + "inner join titles t\n"
                        + "on s.title_id = t.title_id\n"
                        + "inner join stores sto \n"
                        + "on s.stor_id = sto.stor_id\n"
                        + "where sto.stor_id ='"+stor_id+"'";
                ResultSet rs = dao.getData(sql);
                String sql1="SELECT status\n" +
                            "FROM sales s\n" +
                            "WHERE s.stor_id='"+stor_id+"'";
                ResultSet rs1 = dao.getData(sql1);
               
                request.setAttribute("status", rs1);
                request.setAttribute("rs", rs);
                request.setAttribute("storeid", stor_id);
                request.setAttribute("storename",stor_name);  
                request.getRequestDispatcher("template/admin/storedetail.jsp").forward(request, response);   
            }
             
             
            if (service.equals("searchByName")){                
                String name=(String)request.getParameter("query");
                Vector<stores> vector = dao.searchByName(name);
                request.setAttribute("list", vector);
                request.getRequestDispatcher("template/admin/store.jsp").forward(request, response);               
            }
            
            
            if (service.equals("updateStore")) {
                String submit = request.getParameter("submit");
                if (submit == null) {
                    String storID = request.getParameter("stor_id");
                    String sql = "select * from stores where stor_id ='" + storID + "'";
                    ResultSet rs = dao.getData(sql);
                    String titlePage = "Stores manager";
                    String titleTable = "List of stores";
                    request.setAttribute("vector", rs);
                    request.setAttribute("titlePage", titlePage);
                    request.setAttribute("titleTable", titleTable);
                    dispath(request, response, "template/Update/UpdateStore.jsp");
                } else {
                    String stor_id = request.getParameter("stor_id");
                    String stor_name = request.getParameter("stor_name");
                    String stor_address = request.getParameter("stor_address");
                    String city = request.getParameter("city");
                    String state = request.getParameter("state");
                    String zip = request.getParameter("zip");
                    stores sto = new stores(stor_id, stor_name, stor_address, city, state, zip);
                    dao.updateStore(sto);
                    response.sendRedirect("StoresController");
                }
            }
            if (service.equals("deleteStore")) {                               
                    String storID = request.getParameter("stor_id");                  
                    dao.deleteStor(storID);
                    response.sendRedirect("StoresController");               
            }
        }
    }
    public void dispath(HttpServletRequest request,
            HttpServletResponse response, String page)
            throws IOException, ServletException {
        RequestDispatcher dispath = request.getRequestDispatcher(page);
        dispath.forward(request, response);
    }
// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        
            String service = request.getParameter("go");
            //processRequest(request, response);
            if (service.equals("autoupdate")){
            String status=request.getParameter("content");
            String id=request.getParameter("id");
            int sta=Integer.parseInt(status);
            DAOstores dao = new DAOstores();
            String sql="update sales \n" +
                    "set status="+sta+"\n" +
                    "where stor_id='"+id+"'";
            ResultSet rs = dao.getData(sql);
            
            response.sendRedirect("StoresController");
            }
            else{
                processRequest(request, response);
            }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
