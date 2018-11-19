package pdl_2018.groupeSMKS1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Html extends Extracteur {

	private String url;
	private char delimit;
	private String cheminCSV;
	private String nomCSV;
	private boolean extraHTML;
	private boolean extraWiki;
	private ArrayList<Tableau> lesTableaux;
	private Map<String, Element> lesHtmltab;

	public Html(String url, char delimit, String cheminCSV, String nomCSV, boolean extraHTML, boolean extraWiki) {
		this.url = url;
		this.delimit = delimit;
		this.cheminCSV = cheminCSV;
		this.extraHTML = extraHTML;
		this.extraWiki = extraWiki;
		lesTableaux = new ArrayList<Tableau>();
		lesHtmltab = new HashMap<String, Element>();
	}

	@Override
	public void removeTableau() {
	}

	@Override
	public String getNomTableau() {
		return "";
	}

	@Override
	public void addTableau(Tableau leTableau) {
		if (!lesTableaux.contains(leTableau)) {
			lesTableaux.add(leTableau);
		}
	}

	@Override
	public Tableau constructeurTableau(char delimit, String cheminCSV, String NomCSV, boolean extraHTML,
			boolean extraWiki) {
		return new Tableau();
	}

	/**
	 * 
	 * @return le delimiteur choisit par l'utilisateur
	 */
	public char getDelimit() {
		return this.delimit;
	}

	/**
	 * 
	 * @return le chemin de sauvegarde du fichier choisit par l'utilisateur
	 */
	public String getCheminCSV() {
		return this.cheminCSV;
	}

	/**
	 * 
	 * @return le nom du fichier CSV choisit par l'utilisateur
	 */
	public String getNomCSV() {
		return this.getNomCSV();
	}

	/**
	 * 
	 * @return un booleen qui indique si l'extraction doit �etre faite en HTML
	 *         (true) ou non (false)
	 */
	public boolean getExtraHTML() {
		return this.extraHTML;
	}

	/**
	 * 
	 * @return Un booleen qui indique si l'extraction doit etre faite en
	 *         wikicode(true) ou non (false)
	 */
	public boolean getExtraWiki() {
		return this.extraWiki;
	}

	public void recuperationPage() {
		try {
			Document doc = Jsoup.connect(url).get();
			// System.out.println(doc);

			Elements table = doc.select(".wikitable");
			for (int i = 0; i < table.size(); i++) {
				String nom = "";

				Elements caption = table.get(i).getElementsByTag("caption");
				if (caption.isEmpty()) {
					nom = "table" + i;
				} else {
					nom = caption.text();
				}
				lesHtmltab.put(nom, table.get(i)); // cle : titre du tableau

			}
			// System.out.println(table);
			TraitementMap();

		} catch (IOException e) {
			System.out.println(e.getMessage());

		}
	}

	public void TraitementMap() {
		Set cles = lesHtmltab.keySet();
		Iterator<String> it = cles.iterator();
		int counter = 0;

		while (it.hasNext()) {
			String cle = it.next();
			System.out.println(cle);
			Element ensemble = lesHtmltab.get(cle);
			boolean tabcreated = false;

			Elements tr = ensemble.getElementsByTag("tr");
			// System.out.println(tr.size());

			String[][] tab = null;
			int i = 0;
			System.out.println(tr.size());

			for (Element e : tr) {
				Elements th = e.getElementsByTag("th");
				Elements td = e.getElementsByTag("td");

				// if (th.size() == td.size()) { // Cas tableau avec nom colonnes qui
				// apparaissent plusieurs fois
				// (cf equipe de france masculine de foot,Parcours de l'�quipe de France en
				// championnat d'Europe

				if (!tabcreated) {

					int nbCol = 0;

					if (!th.isEmpty()) {
						nbCol = th.size();
					} else {
						nbCol = td.size();
					}
					tab = new String[tr.size()][nbCol];
					System.out.println("th" + th.size());
					System.out.println("td" + td.size());
					System.out.println(nbCol);
					tabcreated = true;
				}

				int j = 0;

				for (Element f : th) {
					tab[i][j] = f.text();
					//System.out.println(tab[i][j] + " " + "i : " + i + " j : " + j);

					j++;
				}

				for (Element g : td) {

					String current = g.text();
					// System.out.println(current);

					String cell = g.attr("rowspan");

					if (cell != "") {

						int y = Integer.parseInt(cell);

						// System.out.println("test "+ y);
						tab = Fusion(tab, i, j, y, current, true);
						


					}
					
					String cellcol = g.attr("colspan");
					if (g.attr("colspan") != "") {

						int x = Integer.parseInt(cellcol);
						

						// System.out.println("test "+ y);
						tab = Fusion(tab, i, j, x, current, false);
						


					}

					if (tab[i][j] == null) {
						if (g.hasText()) {
							tab[i][j] = g.text();
						}
						if (getUrlImage(g) != "")
							tab[i][j] = tab[i][j] + " " + getUrlImage(g);
						System.out.println("FUSION2" + " "  + tab[i][j] + " " + "i : " + i + " j : " + j);

						j++;
					} else {
						while (tab[i][j] != null) {
							System.out.println("FUSION" + " "  + tab[i][j] + " " + "i : " + i + " j : " + j);
							j++;
						}

					}

				}
				i++;
			}

			/*
			 * for(int a=0; a<tab.length; a++) { for(int b = 0; b<tab[a].length; b++) {
			 * System.out.println(tab[a][b] +" " + "i : " + a + " j : " + b ); } }
			 */

			// }
			Tableau t = new Tableau(this.delimit, this.cheminCSV, this.nomCSV, tab, cle);
		}

	}

	public String getUrlImage(Element g) {

		Elements image = g.getElementsByTag("a");

		for (Element im : image) {
			if (im.attr("class").contains("image")) {
				im.attr("href");
				return im.attr("href");
			}

		}
		return "";
	}

	public String[][] Fusion(String[][] tab, int i, int j, int y, String current, boolean vertical) {
		if (vertical) {
			while (i < y) {
				i++;
				tab[i][j] = current;
				//System.out.println(tab[i][j] + " " + "i : " + i + " j : " + j);
				

			}

		}

		else {
			while (j < y) {
				System.out.println(j);
				
				j++;
				//tab[i][j] = current;
				
				//System.out.println(tab[i][j] +" " + "i : "+ i + " j : "+ j);
			}

		}
		return tab;
	}

	public static void main(String[] args) {
		Html t = new Html("https://fr.wikipedia.org/wiki/Vialfr%C3%A8", ';', "chemin", "nomCSV", true, false);
		t.recuperationPage();

	}

}
