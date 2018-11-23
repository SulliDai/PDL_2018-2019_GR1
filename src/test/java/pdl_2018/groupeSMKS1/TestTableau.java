package pdl_2018.groupeSMKS1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestTableau {
	
	
	
	/**
	 * Test parametre tableau
	 */
	@Test
	public void testObjectTableau() {
		
		String tab[][] = { { "0", "2a", "4h", "6", "fds8" }, { "sQ1", "3", "GRE5", "7", "D9" } };

		Tableau csv = new Tableau(';', "C:/Users/sullivand/Music/Desktop/Nouveau dossier/", "WikiMatrix", tab,"tableau");
		Assertions.assertEquals(csv.getDelimit(), ';');
		Assertions.assertEquals(csv.getCheminCsv(), "C:/Users/sullivand/Music/Desktop/Nouveau dossier/");
		Assertions.assertEquals(csv.getNomCsv(), "WikiMatrix");
		System.out.println(csv.getTableauToString());
		Assertions.assertEquals(csv.getTableauToString(),"[0, 2a, 4h, 6, fds8][sQ1, 3, GRE5, 7, D9]");

	}

}
