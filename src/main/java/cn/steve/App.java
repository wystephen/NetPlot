package cn.steve;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.javafx.JavaFXChartFactory;
import org.jzy3d.javafx.JavaFXRenderer3d;
import org.jzy3d.javafx.controllers.mouse.JavaFXCameraMouseController;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
//import org.jzy3d.plot3d.primitives.*;


import java.lang.Math.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Showing how to pipe an offscreen Jzy3d chart image to a JavaFX ImageView.
 * <p>
 * {@link JavaFXChartFactory} delivers dedicated  {@link JavaFXCameraMouseController}
 * and {@link JavaFXRenderer3d}
 * <p>
 * Support
 * Rotation control with left mouse button hold+drag
 * Scaling scene using mouse wheel
 * Animation (camera rotation with thread)
 * <p>
 * TODO :
 * Mouse right click shift
 * Keyboard support (rotate/shift, etc)
 *
 * @author Martin Pernollet
 */
public class App extends Application {


    static AWTChart global_chart;
    static float attitude = 0.0F;
    static AddLine al;




    public static void main(String[] args) {
        Thread a = new Thread(new AddLine());
        a.start();


        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle(App.class.getSimpleName());

        // Jzy3d
        JavaFXChartFactory factory = new JavaFXChartFactory();
        global_chart = getDemoChart(factory, "offscreen");
        ImageView imageView = factory.bindImageView(global_chart);

        // JavaFX
        StackPane pane = new StackPane();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
        pane.getChildren().add(imageView);

        factory.addSceneSizeChangedListener(global_chart, scene);

        stage.setWidth(500);
        stage.setHeight(500);
    }

    private AWTChart getDemoChart(JavaFXChartFactory factory, String toolkit) {
        // -------------------------------
        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return x * sin(x * y);
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-3, 3);
        int steps = 80;

        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(mapper, range, steps);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);

//        Scatter[] scatters = new Scatter[10];
//
//        for(int i=0;i<10;++i)
//        {
//            scatters[i] = new Scatter(new Coord3d(i,i,i),new Color(0.9F,0.2F,0.7F),1.0);
//        }

        // -------------------------------
        // Create a chart
        Quality quality = Quality.Advanced;
        //quality.setSmoothPolygon(true);
        //quality.setAnimated(true);

        // let factory bind mouse and keyboard controllers to JavaFX node
        AWTChart chart = (AWTChart) factory.newChart(quality, toolkit);
        chart.getScene().getGraph().add(surface);

        Coord3d[] cs = new Coord3d[1000];
        for (int i = 0; i < 1000; ++i) {
            cs[i] = new Coord3d(i * 0.01F, i * 0.01F, i * 0.01F);
        }

        Scatter ts = new Scatter(cs);//,new Color(0.9F,0.2F,0.7F),2.0F);

        chart.getScene().getGraph().add(ts);

//        for(int i=0;i<10;++i)
//        {
//            Coord3d[]  cs = new Coord3d[1];
//            cs[0].add(i,i,i);
//            Scatter ts = new Scatter(cs);//,new Color(0.9F,0.2F,0.7F),2.0F);
////            chart.getScene().getGraph().add(ts);
//        }

        return chart;
    }
    static class AddLine implements Runnable {
        public AddLine(){

        }

        public void run() {
            while (true) {
                if (global_chart!=null) {
                    Coord3d[] cs = new Coord3d[1000];
                    for (int i = 0; i < 1000; ++i) {
                        cs[i] = new Coord3d(i * 0.01F * sin(attitude), i * 0.01F * cos(attitude), i * 0.01F);
                    }

                    Scatter ts = new Scatter(cs);//,new Color(0.9F,0.2F,0.7F),2.0F);

                    global_chart.getScene().getGraph().add(ts);
                    attitude += 0.01;
//                    global_chart.getScene().getGraph().remove()

                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
