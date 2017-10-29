/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;

import static java.lang.String.format;

/**
 * A sample that demonstrates various mouse and scroll events and their usage.
 * Click the circles and drag them across the screen. Scroll the whole screen.
 * All events are logged to the console.
 *
 * @see javafx.scene.Cursor
 * @see javafx.scene.input.MouseEvent
 * @see javafx.event.EventHandler
 */
public class Hello extends Application {
    //create a console for logging mouse events
    final ListView<String> console = new ListView<String>();
    //create a observableArrayList of logged events that will be listed in console
    final ObservableList<String> consoleObservableList = FXCollections.observableArrayList();
    {
        //set up the console
        console.setItems(consoleObservableList);
        console.setLayoutY(305);
        console.setPrefSize(500, 195);
    }
    //create a rectangle - (500px X 300px) in which our circles can move
    final Rectangle rectangle = RectangleBuilder.create()
            .width(500).height(300)
            .fill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
                new Stop(10, Color.rgb(156,216,255)),
                new Stop(0, Color.rgb(156,216,255, 0.5))
            }))
            .stroke(Color.RED)
            .build();
    //variables for storing initial position before drag of circle
    private double initX;
    private double initY;
    private Point2D dragAnchor;

    private void init(Stage primaryStage) {
        Group root = new Group();
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 500,500));
        // create circle with method listed below: paramethers: name of the circle, color of the circle, radius
        final Circle circleSmall = createCircle("Blue circle", Color.DODGERBLUE, 25);
        circleSmall.setTranslateX(450);
        circleSmall.setTranslateY(80);
        // and a second, bigger circle
        final Circle circleBig = createCircle("Orange circle", Color.CORAL, 40);
        circleBig.setTranslateX(300);
        circleBig.setTranslateY(150);
        // we can set mouse event to any node, also on the rectangle
        rectangle.setOnMouseMoved(me -> 
                showOnConsole(format("Mouse moved, x: %s, y: %s", 
					me.getX(), me.getY())));

        rectangle.setOnScroll(e -> { double translateX = e.getDeltaX();
                                     double translateY = e.getDeltaY();
                                 

                // reduce the deltas for the circles to stay in the screen
                for (Circle c : new Circle[] { circleSmall, circleBig }) {
                    if (c.getTranslateX() + translateX + c.getRadius() > 450) {
                        translateX = 450 - c.getTranslateX() - c.getRadius();
                    }
                    if (c.getTranslateX() + translateX - c.getRadius() < 0) {
                        translateX = - c.getTranslateX() + c.getRadius();
                    }
                    if (c.getTranslateY() + translateY + c.getRadius() > 300) {
                        translateY = 300 - c.getTranslateY() - c.getRadius();
                    }
                    if (c.getTranslateY() + translateY - c.getRadius() < 0) {
                        translateY = - c.getTranslateY() + c.getRadius();
                    }
                }

                // move the circles
                for (Circle c : new Circle[] { circleSmall, circleBig }) {
                    c.setTranslateX(c.getTranslateX() + translateX);
                    c.setTranslateY(c.getTranslateY() + translateY);
                }

                // log event
                showOnConsole(format("Scrolled, deltaX: %d, deltaY: %d", 
						e.getDeltaX(), e.getDeltaY()));
           });
        // show all the circle , rectangle and console
        root.getChildren().addAll(rectangle, circleBig, circleSmall, console);
    }

    private Circle createCircle(final String name, final Color color, int radius) {
        //create a circle with desired name,  color and radius
        final Circle circle = new Circle(radius, new RadialGradient(0, 0, 0.2, 0.3, 1, true, CycleMethod.NO_CYCLE, new Stop[] {
            new Stop(0, Color.rgb(250,250,255)),
            new Stop(1, color)
        }));
        //add a shadow effect
        circle.setEffect(new InnerShadow(7, color.darker().darker()));
        //change a cursor when it is over circle
        circle.setCursor(Cursor.HAND);
        //add a mouse listeners
        circle.setOnMouseClicked(me -> {
                showOnConsole(format("Clicked on %s, %d times", name, 
					me.getClickCount()));
                //the event will be passed only to the circle which is on front
                me.consume();});
        circle.setOnMouseDragged(me -> {
                double dragX = me.getSceneX() - dragAnchor.getX();
                double dragY = me.getSceneY() - dragAnchor.getY();
                //calculate new position of the circle
                double newXPosition = initX + dragX;
                double newYPosition = initY + dragY;
                //if new position do not exceeds borders of the rectangle, 
				//translate to this position
                if ((newXPosition>=circle.getRadius()) && 
						(newXPosition<=500-circle.getRadius())) {
                    circle.setTranslateX(newXPosition);
                }
                if ((newYPosition>=circle.getRadius()) && 
						(newYPosition<=300-circle.getRadius())){
                    circle.setTranslateY(newYPosition);
                }
                showOnConsole(format("%s was dragged (x: %.2f, y: %.2f)", 
					name, dragX, dragY));
            });
        circle.setOnMouseEntered(me -> {
                //change the z-coordinate of the circle
                circle.toFront();
                showOnConsole(format("Mouse entered ", name));
            });
        circle.setOnMouseExited(mw -> 
                showOnConsole(format("Mouse exited %s", name)));

        circle.setOnMousePressed(me -> {
                 //when mouse is pressed, store initial position
                initX = circle.getTranslateX();
                initY = circle.getTranslateY();
                dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
                showOnConsole(format("Mouse pressed above %s", name));
            });

        circle.setOnMouseReleased(me ->
                showOnConsole(format("Mouse released above %s", name)));

        return circle;
    }

    private void showOnConsole(String text) {
         //if there is 8 items in list, delete first log message, shift other logs and  add a new one to end position
         if (consoleObservableList.size()==8) {
            consoleObservableList.remove(0);
         }
         consoleObservableList.add(text);
    }

    @Override public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX 
     * application. main() serves only as fallback in case the 
     * application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support. NetBeans ignores main().
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}