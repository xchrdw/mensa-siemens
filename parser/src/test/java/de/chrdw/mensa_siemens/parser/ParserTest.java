package de.chrdw.mensa_siemens.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.joox.JOOX;
import org.joox.Match;
import org.junit.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

import de.chrdw.mensa_siemens.parser.model.Food;
import de.chrdw.mensa_siemens.parser.model.Menu;
import de.chrdw.mensa_siemens.parser.model.Restaurant;

public class ParserTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testGetMensas() throws SAXException, IOException {
        QueryProvider queryProvider = Mockito.mock(QueryProvider.class);
        Match xml = JOOX.$(getClass().getResourceAsStream("/test2.xml"));
        Mockito.when(queryProvider.query(Mockito.anyString(), Mockito.anyMap())).thenReturn(xml);

        Parser parser = new Parser(queryProvider);
        List<Restaurant> menus = parser.getRestaurants();
        assertThat(menus, hasSize(74));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetMenus() throws SAXException, IOException {
        QueryProvider queryProvider = Mockito.mock(QueryProvider.class);
        Match xml = JOOX.$(getClass().getResourceAsStream("/test.xml"));
        Mockito.when(queryProvider.query(Mockito.anyString(), Mockito.anyMap())).thenReturn(xml);

        Parser parser = new Parser(queryProvider);
        List<Menu> menus = parser.getMenus(37, LocalDate.of(2014, 12, 6));
        Menu menu = menus.get(0);
        assertThat(menu.getDate(), equalTo(LocalDate.of(2014, 12, 1)));
        assertThat(menus, hasSize(12));

        assertThat(menus.get(5).getFood(), empty());
        assertThat(menus.get(6).getFood(), empty());

        menu = menus.get(7);
        assertThat(menu.getFood(), hasSize(13));
        assertThat(menu.getDate(), equalTo(LocalDate.of(2014, 12, 8)));
        assertThat(menu.getFood().get(0), equalTo(new Food("11", "Salatbuffet à 100g", 0.6f)));
    }

}
