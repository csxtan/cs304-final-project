package ui;

import database.DatabaseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

public class Window extends JFrame {
    private Screen menu;
    private DatabaseHandler dbHandler;
    private static final int SCREENWIDTH = 800;
    private static final int SCREENHEIGHT = 600;

    public static void main(String args[]) {
        new Window();
    }

    private Window() {
        super();

        // Set up database
        dbHandler = new DatabaseHandler();

        // Initialize frame
        setTitle("Query");
        setSize(SCREENWIDTH, SCREENHEIGHT);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);

        buildMenu();

        setContentPane(menu);
        setVisible(true);

        // When window is closed, close the database connection
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                dbHandler.close();
                System.out.println("Window closed.");
                System.exit(0);
            }
        });
    }

    private void buildMenu() {
        menu = new Screen();
        menu.contentPanel.addText("MENU");

        buildShow();
        buildInsert();
        buildDelete();
        buildUpdate();
        buildSelect();
        buildProject();
        buildJoin();
        buildAggregate();
        buildGroup();
        buildDivide();
    }

    private void buildShow() {
        Screen show = initScreen("Show table");
        ContentPanel cp = show.contentPanel;

        Iterator<String> tablesIterator = dbHandler.getTables().iterator();
        JButton button;
        while (tablesIterator.hasNext()) {
            String tableName = tablesIterator.next();
            button = new JButton(tableName);
            button.addActionListener((ActionEvent ae) -> {
                try {
                    renderTable(dbHandler.show(tableName), "Showing table " + tableName, show);
                } catch (SQLException e) {
                    exitOnError(e);
                }
            });
            cp.add(button);
        }
    }

    private void buildInsert() {
        Screen insert = initScreen("Insert new entry into SENDER");
        ContentPanel cp = insert.contentPanel;

        JTextField input_sender_id = cp.addTextField("Sender ID", 4);
        JTextField input_name = cp.addTextField("Name", 20);
        JTextField input_address = cp.addTextField("Address", 30);

        JButton submit = new JButton("Submit");
        cp.add(submit);
        JLabel error_label = cp.addText(" ");

        submit.addActionListener((ActionEvent ae) -> {
            try {
                ResultSet table = dbHandler.insert(
                        input_sender_id.getText(),
                        input_name.getText(),
                        input_address.getText());
                renderTable(table, "Showing SENDER table after insert operation", insert);
                input_sender_id.setText("");
                input_name.setText("");
                input_address.setText("");
                error_label.setText("");
            } catch (SQLException e) {
                error_label.setText("ERROR:" + getErrorMessage(e));
            }
        });
    }

    private void buildDelete() {
        Screen delete = initScreen("Delete entry from VEHICLE");
        ContentPanel cp = delete.contentPanel;

        JTextField input_vehicle_id = cp.addTextField("Vehicle ID of the entry to delete", 6);

        JButton submit = new JButton("Submit");
        cp.add(submit);
        JLabel error_label = cp.addText(" ");

        submit.addActionListener((ActionEvent ae) -> {
            try {
                ResultSet table = dbHandler.delete(input_vehicle_id.getText());
                renderTable(table, "Showing VEHICLE table after delete operation", delete);
                input_vehicle_id.setText("");
                error_label.setText("");
            } catch (SQLException e) {
                error_label.setText("ERROR:" + getErrorMessage(e));
            }
        });
    }

    private void buildUpdate() {
        Screen update = initScreen("Update entry in SENDER");
        ContentPanel cp = update.contentPanel;

        JTextField input_sender_id = cp.addTextField("Sender ID of the entry to update", 4);
        JTextField input_name = cp.addTextField("New Name", 20);
        JTextField input_address = cp.addTextField("New Address", 30);

        JButton submit = new JButton("Submit");
        cp.add(submit);
        JLabel error_label = cp.addText(" ");

        submit.addActionListener((ActionEvent ae) -> {
            try {
                ResultSet table = dbHandler.update(
                        input_sender_id.getText(),
                        input_name.getText(),
                        input_address.getText());
                renderTable(table, "Showing SENDER table after update operation", update);
                input_sender_id.setText("");
                input_name.setText("");
                input_address.setText("");
                error_label.setText("");
            } catch (SQLException e) {
                error_label.setText("ERROR:" + getErrorMessage(e));
            }
        });
    }

    private void buildSelect() {
        Screen select = initScreen("Select from PACKAGE by weight");
        ContentPanel cp = select.contentPanel;
        cp.addText("Get the package IDs of all packages above a weight threshold");

        JTextField input_weight = cp.addTextField("Weight threshold", 10);

        JButton submit = new JButton("Submit");
        cp.add(submit);
        JLabel error_label = cp.addText(" ");

        submit.addActionListener((ActionEvent ae) -> {
            if (isNonnegativeInteger(input_weight.getText())) {
                try {
                    ResultSet table = dbHandler.select(input_weight.getText());
                    renderTable(table,
                            "Showing package IDs of all packages with weight >= " + input_weight.getText(),
                            select);
                    input_weight.setText("");
                    error_label.setText("");
                } catch (SQLException e) {
                    error_label.setText("ERROR:" + getErrorMessage(e));
                }
            } else {
                error_label.setText("ERROR: price threshold must be a nonnegative integer.");
            }
        });
    }

    private void buildProject() {
        Screen project = initScreen("Project TRACKINGINFORMATION onto column");
        ContentPanel cp = project.contentPanel;

        try {
            ResultSet vehicleTable = dbHandler.show("TrackingInformation");
            ResultSetMetaData metadata = vehicleTable.getMetaData();
            String columnName;
            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                columnName = metadata.getColumnName(i);
                cp.add(_projectColumnButton(columnName, project));
            }
        } catch (SQLException e) {
            exitOnError(e);
        }
    }

    // Helper method for buildProject()
    // Creates a button corresponding to each column that we can project onto
    private JButton _projectColumnButton(String columnName, Screen project) {
        JButton button = new JButton(columnName);
        button.addActionListener((ActionEvent ae) -> {
            try {
                ResultSet projectedTable = dbHandler.project(columnName);
                renderTable(projectedTable, "Showing column " + columnName + " of TRACKINGINFORMATION", project);
            } catch (SQLException e) {
                exitOnError(e);
            }
        });
        return button;
    }

    private void buildJoin() {
        Screen join = initScreen("Select from PACKAGE by price (JOIN)");
        ContentPanel cp = join.contentPanel;
        cp.addText("Get the package IDs of all packages above a price threshold");

        JTextField input_price = cp.addTextField("Price threshold", 10);

        JButton submit = new JButton("Submit");
        cp.add(submit);
        JLabel error_label = cp.addText(" ");

        submit.addActionListener((ActionEvent ae) -> {
            if (isNonnegativeInteger(input_price.getText())) {
                try {
                    ResultSet table = dbHandler.join(input_price.getText());
                    renderTable(table,
                            "Showing package IDs of all packages with price >= " + input_price.getText(),
                            join);
                    input_price.setText("");
                    error_label.setText("");
                } catch (SQLException e) {
                    error_label.setText("ERROR:" + getErrorMessage(e));
                }
            } else {
                error_label.setText("ERROR: price threshold must be a nonnegative integer.");
            }
        });
    }

    private void buildAggregate() {
        Screen aggregate = initScreen("Count number of entries in table");
        ContentPanel cp = aggregate.contentPanel;

        Iterator<String> tablesIterator = dbHandler.getTables().iterator();
        JButton button;
        while (tablesIterator.hasNext()) {
            String tableName = tablesIterator.next();
            button = new JButton(tableName);
            button.addActionListener((ActionEvent ae) -> {
                try {
                    renderTable(dbHandler.aggregate(tableName), "Counting entries in " + tableName, aggregate);
                } catch (SQLException e) {
                    exitOnError(e);
                }
            });
            cp.add(button);
        }
    }

    private void buildGroup() {
        JButton group = new JButton("Count how many vehicles each driver drives");
        group.addActionListener((ActionEvent ae) -> {
            try {
                renderTable(dbHandler.group(), "Showing table DRIVES, grouped by employee_id", null);
            } catch (SQLException e) {
                exitOnError(e);
            }
        });
        menu.contentPanel.add(group);
    }

    private void buildDivide() {
        JButton divide = new JButton("Find all drivers which drive all vehicles");
        divide.addActionListener((ActionEvent ae) -> {
            try {
                renderTable(dbHandler.divide(), "Showing employee IDs of drivers which drive all vehicles", null);
            } catch (SQLException e) {
                exitOnError(e);
            }
        });
        menu.contentPanel.add(divide);
    }

    // Takes a ResultSet (from dbHandler) and renders it as a table
    // Switches screens to the rendering screen with the given header
    // Adds a back button to the given screen
    private void renderTable(ResultSet table, String header, Screen backScreen) {
        Screen screen = new Screen();
        ContentPanel cp = screen.contentPanel;
        cp.add(buttonTo(menu, "<< Back to menu"));
        if (backScreen != null) {
            cp.add(buttonTo(backScreen, "<< Back"));
        }
        cp.addText(header + ":");

        int cols;
        int rows = 0;
        try {
            cols = table.getMetaData().getColumnCount();
            ContentPanel rowPanel = new ContentPanel(new GridLayout(1, 5));
            for (int i = 1; i <= cols; i++) {
                rowPanel.addText(table.getMetaData().getColumnName(i));
            }
            cp.add(rowPanel);
            while (table.next()) {
                rowPanel = new ContentPanel(new GridLayout(1, 5));
                for (int i = 1; i <= cols; i++) {
                    rowPanel.addText(table.getString(i));
                }
                cp.add(rowPanel);
                rows++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        cp.setLayout(new GridLayout(rows + 4,1));
        screen.add(cp, BorderLayout.NORTH);

        this.setContentPane(screen);
        this.validate();
    }



    // HELPER METHODS

    // Initializes a screen with the given title
    // Links screen to menu (navigation buttons both ways)
    private Screen initScreen(String screenTitle) {
        Screen screen = new Screen();
        linkToMenu(screen, screenTitle);
        screen.contentPanel.addText(screenTitle + ":");
        return screen;
    }

    // Returns a button which will lead to the given screen
    private JButton buttonTo(Screen screen, String name) {
        JButton to  = new JButton(name);
        to.addActionListener((ActionEvent e) -> {
            Window.this.setContentPane(screen);
            Window.this.validate();
        });
        to.setPreferredSize(new Dimension(SCREENWIDTH, 40));
        return to;
    }

    // Links a screen to menu (nagivation buttons both ways)
    private void linkToMenu(Screen s, String name) {
        menu.contentPanel.add(buttonTo(s, name));
        s.contentPanel.add(buttonTo(menu, "<< Back to menu"));
    }

    // Checks whether a string represents a nonnegative integer
    private boolean isNonnegativeInteger(String s) {
        Integer sI;
        try {
            sI = Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return sI >= 0;
    }

    // Returns ORA error message (to be printed on gui)
    // To be used on user input errors
    private String getErrorMessage(SQLException e) {
        return e.getMessage().substring(10);
    }

    // Prints error in terminal and exits
    // To be used on fatal errors (for debugging purposes)
    private void exitOnError(SQLException e) {
        System.out.println(e.getMessage());
        System.exit(0);
    }
}
