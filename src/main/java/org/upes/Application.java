package org.upes;

import org.upes.controller.Controller;
import org.upes.model.ComputeModel;
import org.upes.model.JsonOp;
import org.upes.model.SqlOp;
import org.upes.view.View;

/**
 * Created by eadgyo on 27/06/17.
 */
public class Application
{
    // View
    private View view;

    // ComputeModel
    private ComputeModel computeModel;

    // Controller
    private Controller controller;

    private JsonOp jsonOp;

    private SqlOp sqlOp;

    public Application()
    {

        jsonOp = new JsonOp();
        sqlOp = new SqlOp();
        view = new View();
        computeModel = new ComputeModel(jsonOp , sqlOp);
        controller = new Controller(view, computeModel , jsonOp ,sqlOp);
        view.typeDialog.pack();
        view.askPathView.pack();
        view.scoresView.pack();
        view.scoresView.setLocationRelativeTo(null);
        view.removeDialog.pack();
        view.removeDialog.splitPane.setDividerLocation(view.removeDialog.mapPane.getWidth()*30/100);
        view.removeDialog.setLocationRelativeTo(null);
        launchApp();

    }

    public static void main(String[] args)
    {
        // Start application
        new Application();
    }

    private void launchApp()
    {
        view.startLoading();
        try
        {
            Thread.sleep(Constants.TIME_LOADING);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        view.swapCard();
    }

}
