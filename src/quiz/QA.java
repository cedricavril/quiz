package quiz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// pour tous les del des autres classes, les répercuter ici. 

public class QA implements Manager {

	private int				id,
							id_category,
							id_author;
	private LocalDate 		creationDate;
	private String 			q;
	private String 			details;
	private ArrayList<String>	a = new ArrayList<String>();
	private String 			num_a;
	static public String	NOM_FICHIER;

/**
 * 
 * @param id - id
 * @param q - Question
 * @param a - Answers
 * @param num_a - answer numero
 * @param creationDate - creation date
 * @param id_author - author id
 * @param id_category - categoy id
 */
	public QA(int id, String q, String details, ArrayList<String> a, String num_a, LocalDate creationDate, int id_author, int id_category) {
		this.id = id;
		this.q = q;
		this.details = details;

		for(String s: a) {
			this.a.add(" " + s.trim());
		}
		this.num_a = num_a;
		this.creationDate = creationDate;
		this.id_author = id_author;
		this.id_category = id_category;
	}

	
	
	public QA() {
	// TODO Auto-generated constructor stub
}



	public void saveQA() {

		if (!Manager.loadItemsFromGroup(NOM_FICHIER, this.q).isEmpty()) return;

		Map<Integer, ArrayList<String>> map = new HashMap<>();
		map.put(0, Manager.convStringToArrayListOfStrings(this.q));

		map.put(1, Manager.convStringToArrayListOfStrings(this.details));
		map.put(2, this.a);
		map.put(3, Manager.convStringToArrayListOfStrings(this.num_a));
		map.put(4, Manager.convStringToArrayListOfStrings(getStringFromDate(this.creationDate)));
		map.put(5, Manager.convStringToArrayListOfStrings("" + this.id_author));
		map.put(6, Manager.convStringToArrayListOfStrings("" + this.id_category));

		saveItemsFromGroup(NOM_FICHIER, map, this.id);
		
	}
	
// bof si id est nécessaire au constructeur, remplacer this.id par un appel à ça dès que ça marche et modifier main en conséquence
	static int getId(String q) {	
		return Manager.getIdFromMainFile(NOM_FICHIER, q);
	}
	
	public int getIdCategory() {
		return id_category;
	}
	
	public int getIdAuthor() {
		return id_author;
	}
	
	public String getQ() {
		return q;
	}
	
	public String getNum_a() {
		return num_a;
	}
	
	public ArrayList<String> getA() {
		return a;
	}
	
	public void showAs() {
		for(String s : a) {
			System.out.println(s);
		}
	}
	
	public static QA loadQA(String s) {
		Map<Integer, ArrayList<String>> map = new HashMap<>(Manager.loadItemsFromGroup(NOM_FICHIER, s));

		int id = Integer.parseInt(map.get(0).get(0));
		String q = map.get(1).get(0);
		String details = map.get(2).get(0);
		ArrayList<String> a = map.get(3);
		String num_a = map.get(4).get(0);
		LocalDate creationDate = Manager.getDateFromString(map.get(5).get(0));
		int id_author = Integer.parseInt(map.get(6).get(0));
		int id_category = Integer.parseInt(map.get(7).get(0));
		
		return (new QA(id, q, details, a, num_a, creationDate, id_author, id_category));
	}

	public static ArrayList<QA> loadQAs() {
		ArrayList<QA> qas = new ArrayList<QA>(); 		

		for(String s : Manager.loadFirstContents(NOM_FICHIER).values())
			qas.add(loadQA(s));

		return qas;
	}
	public boolean delQA() {
		// et détruire la category id
		return delItems(NOM_FICHIER, id, 0);
	}

	@Override
	public String toString() {
		return 	"***********************************************************" + System.getProperty("line.separator") + 
				"* QA numéro : " + id + System.getProperty("line.separator") + 
				"* Catégorie numéro : " + id_category + System.getProperty("line.separator") + 
				"* Auteur numéro : " + id_author + System.getProperty("line.separator") + 
				"* Date de création : " + getStringFromDate(creationDate) + System.getProperty("line.separator") + 
				"* Question : " + q + System.getProperty("line.separator") + 
				"* Réponses : " + a + System.getProperty("line.separator") + 
				"* Détails : " + details + System.getProperty("line.separator") + 
				"* Numéro de la réponse juste : " + num_a + System.getProperty("line.separator") + 
				"***********************************************************"+ System.getProperty("line.separator");
	}
}