import Entities.*;

import javax.persistence.*;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;

public class ConsoleInterface {
    private EntityManager em;
    private Scanner sc = new Scanner(System.in);

    public ConsoleInterface() {

    }

    public void showInterface() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Orders");
        em = emf.createEntityManager();
        try {
            do {
                System.out.println("*".repeat(50));
                System.out.print("""
                        Enter item number:
                          1: manage client
                          2: manage product
                          3: manage order
                          4: generate random data in tables
                          5: exit
                        ->\s""");
            } while (processInputDataFromMainInterface());
        } finally {
            sc.close();
            em.close();
            emf.close();
        }
    }

    private boolean processInputDataFromMainInterface() {
        String choice = sc.nextLine();
        boolean result = true;
        switch (choice) {
            case "1" -> showManagerInterface(new Client());
            case "2" -> showManagerInterface(new Product());
            case "3" -> showManagerInterface(new Orders());
            case "4" -> generateTables();
            case "5" -> {
                return false;
            }
            default -> result = false;
        }
        if (!result) {
            System.out.println("Incorrect entered data, try again");
        }
        return true;
    }

    private <T> void showManagerInterface(T table) {
        String name = table.getClass().getSimpleName().toLowerCase();
        if (name.endsWith("s")) {
            name = name.substring(0, name.length() - 1);
        }
        do {
            System.out.println(("" + name.charAt(0)).repeat(50));
            System.out.print(String.format("""
                    Enter item number:
                      1: add %1$s
                      2: change %1$s
                      3: show details
                      4: show %1$ss
                      5: generate random %1$ss
                      6: return
                    ->\s""", name));
        } while (processInputDataForManagerInterface(table));
    }

    private <T> boolean processInputDataForManagerInterface(T table) {
        String choice = sc.nextLine();
        boolean result = true;
        switch (choice) {
            case "1" -> result = addPosition(table, false);
            case "2" -> result = changePosition(table);
            case "3" -> viewDetail(table);
            case "4" -> viewTable(table);
            case "5" -> result = addPosition(table, true);
            case "6" -> {
                return false;
            }
            default -> result = false;
        }
        if (!result) {
            System.out.println("Incorrect entered data, try again");
        }
        return true;
    }

    private <T> T performTransaction(Callable<T> action) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            T result = action.call();
            transaction.commit();

            return result;
        } catch (Exception ex) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            ex.printStackTrace();
            System.out.println("Error, problem with database");
            return null;
        }
    }

    private <T> boolean addPosition(T table, boolean random) {
        try {
            T[] positions = getArray(table, random);
            if (positions == null) {
                return true;
            }
            performTransaction(() -> {
                for (var position : positions) {
                    em.persist(position);
                }
                return null;
            });
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }

    private <T> T[] getArray(T table, boolean random) {
        if (!random) {
            return switch (table.getClass().getSimpleName()) {
                case "Client" -> (T[]) requestDataToAddClient();
                case "Product" -> (T[]) requestDataToAddProduct();
                case "Orders" -> (T[]) requestDataToAddOrder();
                default -> null;
            };
        }
        return requestRandomDataToAdd(table);
    }

    private Client[] requestDataToAddClient() {
        System.out.print("Enter name: ");
        String name = checkInputData(String.class, true, true);
        System.out.print("Enter address: ");
        String address = checkInputData(String.class, true, true);
        System.out.print("Enter phone: ");
        String phone = checkInputData(String.class, true, true);
        return new Client[]{new Client(name, address, phone)};
    }

    private Product[] requestDataToAddProduct() {
        System.out.print("Enter product name: ");
        String productName = checkInputData(String.class, true, true);
        System.out.print("Enter unit name: ");
        String unitName = checkInputData(String.class, true, true);
        System.out.print("Enter unit price: ");
        Double unitPrice = checkInputData(Double.class, true, true);
        return new Product[]{new Product(productName, unitName, unitPrice)};
    }

    private Orders[] requestDataToAddOrder() {
        Client client = findPosition("Enter the id of the client who makes the orders: ", new Client());
        if (client == null) {
            return null;
        }
        List<OrderDetails> orderDetails = showOrderInterface();
        if (orderDetails.size() == 0) {
            return null;
        }
        Orders[] orders = new Orders[]{new Orders("placed", LocalDate.now())};
        for (var p : orderDetails) {
            orders[0].addPosition(p);
        }
        client.addOrder(orders[0]);

        return orders;
    }

    public List<OrderDetails> showOrderInterface() {
        List<OrderDetails> orderDetails = new ArrayList<>();
        do {
            System.out.println("$".repeat(50));
            System.out.print("""
                    Enter item number:
                      1: add product
                      2: show product
                      3: accept the order
                      4: cancel the order
                    ->\s""");
        } while (processInputDataFromOrderInterface(orderDetails) != null);
        return orderDetails;
    }

    private OrderDetails processInputDataFromOrderInterface(List<OrderDetails> orderDetails) {
        String choice = sc.nextLine();
        switch (choice) {
            case "1" -> {
                Product product = findPosition("Enter id of the product: ", new Product());
                OrderDetails position = null;
                if (product != null) {
                    System.out.print("Enter amount: ");
                    Integer amount = checkInputData(Integer.class, true, true);
                    System.out.print("Enter discount percentage: ");
                    Double discount = checkInputData(Double.class, true, true);

                    position = new OrderDetails(amount, discount);
                    product.addPosition(position);
                    orderDetails.add(position);
                }
                return position;
            }
            case "2" -> viewTable(new Product());
            case "3" -> {
                return null;
            }
            case "4" -> {
                orderDetails.clear();
                return null;
            }
        }
        return new OrderDetails();
    }

    private <T> T checkInputData(Class<T> clazz, boolean checkEmpty, boolean throwEx) {
        String result = sc.nextLine();
        if (checkEmpty) {
            if (result.isEmpty()) {
                if (throwEx) {
                    throw new IllegalArgumentException();
                }
                return null;
            }
        }
        T resultValue = (T) switch (clazz.getSimpleName()) {
            case "Integer" -> Integer.parseInt(result);
            case "Double" -> Double.parseDouble(result);
            case "LocalDate" -> LocalDate.parse(result);
            default -> result;
        };
        if (!clazz.getSimpleName().equals("String")) {
            if (result.startsWith("-")) {
                throw new IllegalArgumentException();
            }
        }
        return resultValue;
    }


    private <T> T[] requestRandomDataToAdd(T table) {
        String name = table.getClass().getSimpleName().toLowerCase();
        System.out.print("Enter the number of " + name + "s to be generated: ");
        int number = Integer.parseInt(sc.nextLine());
        T[] result = (T[]) Array.newInstance(table.getClass(), number);
        for (int i = 0; i < result.length; i++) {
            Array.set(result, i, getRandomPosition(table));
        }
        return result;
    }

    private <T> T getRandomPosition(T table) {
        return switch (table.getClass().getSimpleName()) {
            case "Client" -> (T) generateClient();
            case "Product" -> (T) generateProduct();
            case "Orders" -> (T) generateOrder();
            default -> null;
        };
    }

    private Client generateClient() {
        Random random = new Random();
        String[] names = {"Dmytro", "Oleksandr", "Vsevolod", "Vadym", "OLena", "Maria"};
        String[] addresses = {"Lesya Ukrainka Street",
                "Bogdan Khmelnitsky Street",
                "Oles Honchar Street"};
        String name = names[random.nextInt(names.length)];
        String address = addresses[random.nextInt(addresses.length)] +
                ", " + (random.nextInt(20) + 1);
        String phone = "+38050" + (random.nextInt(8999999) + 1000000);

        return new Client(name, address, phone);
    }

    private Product generateProduct() {
        Random random = new Random();
        String[] names = {"Beer", "Vodka", "Brandy", "Rum", "Wine", "Cognac", "Whiskey"};
        String[] brands = {"Johnnie Walker", "Hennessy", "Jack Daniel's", "Guinness",
                "Bud Light", "Sula Rasa Shiraz", "Fratelli SETTE"};
        int temp = random.nextInt(names.length);
        String productName = names[temp] + " " + brands[random.nextInt(brands.length)];
        String unitName = "bottle";
        double unitPrice = (random.nextDouble(30) + 30) * (temp * 8 + 1);
        unitPrice = Math.ceil(unitPrice);
        return new Product(productName, unitName, unitPrice);
    }

    private Orders generateOrder() {
        TypedQuery<Client> queryClients = em.createQuery("SELECT o FROM Client o",
                Client.class);
        List<Client> clientList = queryClients.getResultList();
        TypedQuery<Product> queryProducts = em.createQuery("SELECT o FROM Product o",
                Product.class);
        List<Product> productList = queryProducts.getResultList();

        Random random = new Random();
        String[] statuses = {"placed", "in progress", "done"};
        Orders order;
        OrderDetails orderDetails;

        String status = statuses[random.nextInt(statuses.length)];
        order = new Orders(status, LocalDate.of(random.nextInt(2) + 2021,
                random.nextInt(12) + 1, random.nextInt(28) + 1));
        int positions = random.nextInt(10) + 1;
        for (int i = 0; i < positions; i++) {
            int amount = random.nextInt(100) + 1;
            double discount = (random.nextDouble(30));
            discount = Math.ceil(discount);
            orderDetails = new OrderDetails(amount, discount);

            productList.get(random.nextInt(productList.size())).addPosition(orderDetails);
            order.addPosition(orderDetails);
        }

        clientList.get(random.nextInt(clientList.size())).addOrder(order);

        return order;
    }

    private void generateTables() {
        Client[] client = new Client[10];
        Product[] product = new Product[10];
        for (int i = 0; i < 10; i++) {
            client[i] = generateClient();
            product[i] = generateProduct();
        }
        performTransaction(() -> {
            for (int i = 0; i < 10; i++) {
                em.persist(client[i]);
                em.persist(product[i]);
            }
            return null;
        });
        performTransaction(() -> {
            for (int i = 0; i < 10; i++) {
                em.persist(generateOrder());
            }
            return null;
        });
    }


    private <T> T findPosition(String message, T table) {
        System.out.print(message);
        String name = table.getClass().getSimpleName();
        long id = Long.parseLong(sc.nextLine());
        T position = (T) em.find(table.getClass(), id);
        if (position == null) {
            System.out.println(name + " not found!");
        }
        return position;
    }


    private <T> boolean changePosition(T table) {
        try {
            String name = table.getClass().getSimpleName();
            T position = findPosition("Enter the ID of the " +
                    name.toLowerCase() + " to be changed: ", table);
            if (position != null) {
                return switch (table.getClass().getSimpleName()) {
                    case "Client" -> changeClient((Client) position);
                    case "Product" -> changeProduct((Product) position);
                    case "Orders" -> changeOrder((Orders) position);
                    default -> false;
                };
            }
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    private boolean changeClient(Client position) {
        System.out.print("Enter new name or empty to skip: ");
        String name = sc.nextLine();
        System.out.print("Enter new address or empty to skip: ");
        String address = sc.nextLine();
        System.out.print("Enter new phone or empty to skip: ");
        String phone = sc.nextLine();

        performTransaction(() -> {
            if (!name.isEmpty()) {
                position.setName(name);
            }
            if (!address.isEmpty()) {
                position.setAddress(address);
            }
            if (!phone.isEmpty()) {
                position.setPhone(phone);
            }
            System.out.println("Client changed");
            return null;
        });
        return true;
    }

    private boolean changeProduct(Product position) {
        System.out.print("Enter new product name or empty to skip: ");
        String productName = sc.nextLine();
        System.out.print("Enter new unit name or empty to skip: ");
        String unitName = sc.nextLine();

        performTransaction(() -> {
            if (!productName.isEmpty()) {
                position.setProductName(productName);
            }
            if (!unitName.isEmpty()) {
                position.setUnitName(unitName);
            }
            System.out.println("Product changed");
            return null;
        });
        return true;
    }

    private boolean changeOrder(Orders position) {
        System.out.print("Enter new status or empty to skip: ");
        String status = sc.nextLine();
        System.out.print("Enter new date or empty to skip: ");
        LocalDate date = checkInputData(LocalDate.class, true, false);

        performTransaction(() -> {
            if (!status.isEmpty()) {
                position.setStatus(status);
            }
            if (date != null) {
                position.setDateTime(date);
            }
            System.out.println("Order changed");
            return null;
        });
        return true;
    }


    private <T> void viewTable(T table) {
        String name = table.getClass().getName();
        TypedQuery<FormatObj> query = em.createQuery("SELECT o FROM " + name + " o",
                FormatObj.class);
        viewTable(query);
    }


    private void viewTable(TypedQuery<FormatObj> query) {
        List<FormatObj> list = query.getResultList();
        if (list.size() == 0) {
            System.out.println("There are no such data in the database");
        } else {
            String border = "-".repeat(150);
            System.out.println(border);
            System.out.println(list.get(0).getHeader());
            System.out.println(border);
            for (var a : list) {
                System.out.println(a.getFormattedObject());
            }
            System.out.println(border);
        }
    }

    private <T> void viewDetail(T table) {
        String name = table.getClass().getSimpleName().toLowerCase();
        if (name.endsWith("s")) {
            name = name.substring(0, name.length() - 1);
        }
        T obj = findPosition("Enter id of the " + name + " to show details: ", table);
        if (obj != null) {
            if (name.equals("client")) {
                for (Orders i : ((Client) obj).getOrdersList()) {
                    TypedQuery<FormatObj> query = em.createQuery("SELECT o FROM OrderDetails o " +
                                    "WHERE o.order = :value",
                            FormatObj.class);
                    query.setParameter("value", i);
                    viewTable(query);
                }
            } else {
                TypedQuery<FormatObj> query = em.createQuery("SELECT o FROM OrderDetails o " +
                                "WHERE o." + name + " = :value",
                        FormatObj.class);
                query.setParameter("value", obj);
                viewTable(query);
            }
        }
    }
}
