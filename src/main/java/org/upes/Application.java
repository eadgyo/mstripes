package org.upes;

import org.upes.controller.Controller;
import org.upes.model.Model;
import org.upes.view.View;

/**
 * Created by eadgyo on 27/06/17.
 */
public class Application
{
    // View
    private View view;

    // Model
    private Model model;

    // Controller
    private Controller controller;

    public Application()
    {

        view = new View();
        model = new Model();
        controller = new Controller(view, model);

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
