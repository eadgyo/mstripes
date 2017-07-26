package org.upes;

import org.upes.controller.Controller;
import org.upes.model.ComputeModel;
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

    public Application()
    {

        view = new View();
        computeModel = new ComputeModel();
        controller = new Controller(view, computeModel);
        view.optionsDialog.pack();
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
