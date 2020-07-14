package quiz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Author implements Manager {

	private int 			id;
	private String 			firstName,
							lastName;
	private LocalDate 		registrationDate;
	public static String	NOM_FICHIER;

// crée ou récupère un author en lui attribuant un id selon ce que contient le fichier
	public Author(String lastName, String firstName, LocalDate registrationDate) {
		FileManager.openOrCreateFile(NOM_FICHIER);
		this.id = Manager.getIdFromMainFile(NOM_FICHIER, lastName + "\n" + firstName + "\n" + getStringFromDate(registrationDate));
		this.firstName = firstName;
		this.lastName = lastName;
		this.registrationDate = registrationDate;
	}

	public Author(int id, ArrayList<String> data) {
		this.id = id;
		this.lastName = data.get(0);
		this.firstName = data.get(1);
		this.registrationDate = Manager.getDateFromString(data.get(2));
	}

	public int getId() {
		return this.id;
	}
	
	public boolean saveAuthor() {
			Map<Integer, ArrayList<String>> items = new HashMap<Integer, ArrayList<String>>();
			ArrayList<String> sal = new ArrayList<String>();

			String item = lastName + "\n" + firstName + "\n" +  getStringFromDate(registrationDate);

			sal.add(item);
			items.put(0, sal);

			return saveItemsFromGroup(NOM_FICHIER, items, id);
	}

	public static Author loadAuthor(String s) {
		FileManager.openOrCreateFile(NOM_FICHIER);
		Map<Integer, ArrayList<String>> map = Manager.loadItemsFromGroup(NOM_FICHIER, s);

		int id = Integer.parseInt(map.get(0).get(0));
		ArrayList<String> data = map.get(1);

		String[] sa = data.get(0).split("\n");
		ArrayList<String> sal = new ArrayList<String>();
		for(String item: sa) sal.add(item);
		
		Author a = new Author(id, sal);
		return a;
	}

	public static Author loadAuthor(int id) {
		return loadAuthor(Manager.loadFirstContent(NOM_FICHIER, id));
	}
	

	public static List<Author> loadAuthors() {
		ArrayList<Author> authors = new ArrayList<Author>();
		FileManager.openOrCreateFile(NOM_FICHIER);
		for(String s : Manager.loadFirstContents(NOM_FICHIER).values()) {
			authors.add(loadAuthor(s));
		}
		
		return authors;
	}
	
	public static void showAuthors(List<Author> authors) {
		if(authors.isEmpty()) return;
		for(Author a : authors) {
			System.out.println(a);
		}
	}
	
	public boolean delAuthor() {
		return delItems(NOM_FICHIER, id, 0);
	}
	
	public static Author createAuthor() {
		String firstName = Manager.ask("prénom : ");
		String lastName = Manager.ask("nom : ");
		return new Author(lastName, firstName, LocalDate.now());
	}
	
	@Override
	public String toString() {
		return id + " : " + firstName + " " + lastName;
	}
}
