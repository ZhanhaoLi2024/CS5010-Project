import org.junit.Before;
import org.junit.Test;

import item.Item;
import place.Place;
import place.PlaceModel;
import town.TownModel;

import java.io.IOException;
import static org.junit.Assert.*;

public class TownModelTest {

    private TownModel town;

    @Before
    public void setUp() throws IOException {
        // 创建一个TownModel对象，加载测试用的SmallTownWorld.txt文件
        town = new TownModel("res/SmallTownWorld.txt");
    }

    @Test
    public void testLoadTown() {
        // 确认城镇对象加载成功
        assertNotNull(town);
        
        // 验证加载的地方数量是否正确
        assertEquals(20, town.getPlaces().size());
        
        // 验证地方名称和坐标是否正确
        Place firstPlace = town.getPlaces().get(0);
        assertEquals("Park", firstPlace.getName());
        assertEquals(0, ((PlaceModel)firstPlace).getRow1());
        assertEquals(0, ((PlaceModel)firstPlace).getCol1());
        assertEquals(2, ((PlaceModel)firstPlace).getRow2());
        assertEquals(3, ((PlaceModel)firstPlace).getCol2());

        // 验证其他地方
        Place secondPlace = town.getPlaces().get(1);
        assertEquals("Grocery Store", secondPlace.getName());
    }

    @Test
    public void testLoadItems() {
        // 验证物品加载正确
        Place firstPlace = town.getPlaces().get(0);
        assertEquals(1, firstPlace.getItems().size());

        // 验证物品信息是否正确
        Item firstItem = firstPlace.getItems().get(0);
        assertEquals("Toy Ball", firstItem.getName());
        assertEquals(8, firstItem.getDamage());

        // 验证第二个地方的物品
        Place secondPlace = town.getPlaces().get(1);
        Item secondItem = secondPlace.getItems().get(0);
        assertEquals("Shopping Cart", secondItem.getName());
        assertEquals(12, secondItem.getDamage());
    }

    @Test
    public void testCharacterMovement() {
        // 验证角色最初在第一个地方
        Place firstPlace = town.getCharacter().getCurrentPlace();
        assertEquals("Park", firstPlace.getName());

        // 移动角色并验证角色移动到下一个地方
        town.moveCharacter();
        Place secondPlace = town.getCharacter().getCurrentPlace();
        assertEquals("Grocery Store", secondPlace.getName());

        // 继续移动并验证
        town.moveCharacter();
        Place thirdPlace = town.getCharacter().getCurrentPlace();
        assertEquals("School", thirdPlace.getName());
    }

    @Test
    public void testCharacterLoopMovement() {
        // 模拟角色移动到最后一个地方
        for (int i = 0; i < 19; i++) {
            town.moveCharacter();
        }
        Place lastPlace = town.getCharacter().getCurrentPlace();
        assertEquals("Community Center", lastPlace.getName());

        // 再移动一次，验证角色循环回到第一个地方
        town.moveCharacter();
        Place firstPlace = town.getCharacter().getCurrentPlace();
        assertEquals("Park", firstPlace.getName());
    }

    @Test
    public void testDisplayPlaceInfo() {
        // 捕获房间信息的显示
        Place firstPlace = town.getCharacter().getCurrentPlace();
        
        // 验证地方的名称、物品和邻居信息是否正确显示
        town.displayPlaceInfo(firstPlace); // 你可以捕获System.out来测试这个输出
        
        assertEquals("Park", firstPlace.getName());
        assertFalse(firstPlace.getItems().isEmpty());
        assertEquals("Toy Ball", firstPlace.getItems().get(0).getName());
    }
}