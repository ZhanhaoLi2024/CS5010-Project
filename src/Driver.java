//import java.io.IOException;
//import place.Place;
//import town.TownModel;
//
//public class Driver {
//    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.out.println("Usage: java Driver <world-specification-file>");
//            return;
//        }
//
//        String filename = args[0];
//
//        try {
//            TownModel town = new TownModel(filename);
//
//            System.out.println("\n=== Initial Town and Character Information ===");
//            Place currentPlace = town.getCharacter().getCurrentPlace();
//            town.displayPlaceInfo(currentPlace);
//            town.getItems();
//
//            System.out.println("\n=== Moving Character ===");
//            town.moveCharacter();
//            currentPlace = town.getCharacter().getCurrentPlace();
//            town.displayPlaceInfo(currentPlace);
//
//            town.moveCharacter();
//            currentPlace = town.getCharacter().getCurrentPlace();
//            town.displayPlaceInfo(currentPlace);
//
//
//            System.out.println("\n=== Testing Loop Movement ===");
//            for (int i = 0; i < town.getPlaces().size() - 1; i++) {
//                town.moveCharacter();
//            }
//            currentPlace = town.getCharacter().getCurrentPlace();
//            town.displayPlaceInfo(currentPlace);
//            town.moveCharacter(); // back to the first place
//            currentPlace = town.getCharacter().getCurrentPlace();
//            town.displayPlaceInfo(currentPlace);
//
//        } catch (IOException e) {
//            System.out.println("Error loading the town from file: " + e.getMessage());
//        }
//    }
//}


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import place.Place;
import town.TownModel;

import javax.imageio.ImageIO;

public class Driver {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TownModel town = null;

        try {
            // 加载世界规格文件并创建 TownModel 对象
            town = new TownModel("res/SmallTownWorld.txt");
        } catch (IOException e) {
            System.out.println("Error loading the town: " + e.getMessage());
            return;
        }

        System.out.println("Welcome to Kill Doctor Lucky!");
        displayMapInfo(town);  // 打印地图的所有信息

        while (true) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1. Move target character");
            System.out.println("2. Describe target's current space");
            System.out.println("3. Describe neighbors of current space");
//            System.out.println("4. Describe space by index");
//            System.out.println("5. Describe neighbors by index");
//            System.out.println("6. Out map");
            System.out.println("0. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    moveTargetCharacter(town);
                    break;
                case 2:
                    describeCurrentSpace(town);
                    break;
                case 3:
                    describeNeighborsOfCurrentSpace(town);
                    break;
//                case 4:
//                    describeSpaceByIndex(town, scanner);
//                    break;
//                case 5:
//                    describeNeighborsByIndex(town, scanner);
//                    break;
//                case 6:
//                    outMap(town);
//                    break;
                case 0:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    // 打印所有地图信息，包括房间和道具
    private static void displayMapInfo(TownModel town) {
        System.out.println("=== Map Information ===");
        for (Place place : town.getPlaces()) {
            System.out.println("Place: " + place.getName());
            System.out.println("Items in the place:");
            place.getItems().forEach(item -> System.out.println("- " + item.getName() + " (Damage: " + item.getDamage() + ")"));
            System.out.println("Neighboring places:");
            place.getNeighbors().forEach(neighbor -> System.out.println("- " + neighbor.getName()));
            System.out.println();
        }
    }

    // 移动目标角色
    private static void moveTargetCharacter(TownModel town) {
        System.out.println("Moving the target character...");
        town.moveCharacter();
        describeCurrentSpace(town);
    }

    // 描述目标角色所在的当前房间
    private static void describeCurrentSpace(TownModel town) {
        Place currentPlace = town.getCharacter().getCurrentPlace();
        System.out.println("Target character is in: " + currentPlace.getName());
        System.out.println("Items in the place:");
        currentPlace.getItems().forEach(item -> System.out.println("- " + item.getName() + " (Damage: " + item.getDamage() + ")"));
    }

    // 描述当前房间的邻居
    private static void describeNeighborsOfCurrentSpace(TownModel town) {
        Place currentPlace = town.getCharacter().getCurrentPlace();
        System.out.println("Neighbors of " + currentPlace.getName() + ":");
        currentPlace.getNeighbors().forEach(neighbor -> System.out.println("- " + neighbor.getName()));
    }

    // 通过索引描述房间
    private static void describeSpaceByIndex(TownModel town, Scanner scanner) {
        System.out.println("Enter the index of the space (0 to " + (town.getPlaces().size() - 1) + "):");
        int index = scanner.nextInt();
        if (index < 0 || index >= town.getPlaces().size()) {
            System.out.println("Invalid index.");
            return;
        }
        Place place = town.getPlaces().get(index);
        System.out.println("Place: " + place.getName());
        System.out.println("Items in the place:");
        place.getItems().forEach(item -> System.out.println("- " + item.getName() + " (Damage: " + item.getDamage() + ")"));
    }

    // 通过索引描述房间的邻居
    private static void describeNeighborsByIndex(TownModel town, Scanner scanner) {
        System.out.println("Enter the index of the space (0 to " + (town.getPlaces().size() - 1) + "):");
        int index = scanner.nextInt();
        if (index < 0 || index >= town.getPlaces().size()) {
            System.out.println("Invalid index.");
            return;
        }
        Place place = town.getPlaces().get(index);
        System.out.println("Neighbors of " + place.getName() + ":");
        place.getNeighbors().forEach(neighbor -> System.out.println("- " + neighbor.getName()));
    }

    // 输出地图到图像文件
    private static void outMap(TownModel town) {
        int width = 800;  // 设置图像宽度
        int height = 600;  // 设置图像高度
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.getGraphics();

        // 设置背景颜色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 设置房间颜色和绘制矩形的样式
        g.setColor(Color.BLUE);

        // 假设每个房间用矩形表示，按照一些坐标绘制
        int roomWidth = 100;
        int roomHeight = 100;
        int xOffset = 50;
        int yOffset = 50;

        for (int i = 0; i < town.getPlaces().size(); i++) {
            Place place = town.getPlaces().get(i);

            // 简单的房间布局方式，您可以根据具体世界设计更复杂的布局
            int x = (i % 4) * (roomWidth + xOffset);
            int y = (i / 4) * (roomHeight + yOffset);

            // 绘制房间矩形
            g.setColor(Color.BLUE);
            g.fillRect(x, y, roomWidth, roomHeight);

            // 在房间内绘制房间名称
            g.setColor(Color.BLACK);
            g.drawString(place.getName(), x + 10, y + 20);
        }

        // 保存图像为 PNG 文件
        try {
            ImageIO.write(bufferedImage, "png", new File("res/map_output.png"));
            System.out.println("Map image saved to res/map_output.png");
        } catch (IOException e) {
            System.out.println("Error saving map image: " + e.getMessage());
        }

        // 释放资源
        g.dispose();
    }
}