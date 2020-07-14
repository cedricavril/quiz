package quiz;

import java.util.ArrayList;
import java.util.Scanner;

public class Category implements Manager{

	private int id;
	// caution : handling one category at a time during the execution
	static public ArrayList<Integer> 	parentsIds = new ArrayList<Integer>();
	private String 						name;
	static public String				NOM_FICHIER;
	
	
	public Category(String name) {
		FileManager.openOrCreateFile(NOM_FICHIER);
		this.id = getIdFromBunch(NOM_FICHIER, name);
		this.name = name;
		if (parentsIds != null) parentsIds.clear();
		parentsIds = getIdsFromBunch(NOM_FICHIER, this.id);
	}

	public Category(String name, ArrayList<Integer> ids) {
		FileManager.openOrCreateFile(NOM_FICHIER);
		this.id = getIdFromBunch(NOM_FICHIER, name);
		this.name = name;
		parentsIds = new ArrayList<Integer>();
		parentsIds.addAll(ids);
	}

	public String getName() {
		return this.name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void saveCategory() {
		saveSubItem(NOM_FICHIER, this.getName(), Category.parentsIds, this.id);
	}

	public static ArrayList<Category> loadCategories(String catName) {
		ArrayList<Category> res = new ArrayList<Category>();
		Category c = new Category(catName);
		for(String categoryName : c.loadItemsNameList(NOM_FICHIER)) {
			categoryName = categoryName.replaceFirst("(\\d* : )", "");
			res.add(new Category(categoryName));
		}
		return res;
	}
	
	public static ArrayList<Category> showMainCategories(String catName) {
		ArrayList<Category> cal = loadCategories(catName);
		System.out.println("catégories principales : ");
		for(Category c : cal) {
			c.parentsIds = c.getIdsFromBunch("categories", c.getId());
			if (c.parentsIds == null) System.out.println(c.getId() + " : " + c.getName());
		}
		return cal;
	}

	public static int menuSubCategories(ArrayList<Category> cal, int id) {
		// TODO error handling for int entered out of bounds of printlned ids. Or build an id list or array as there are gaps. Same for menuAuthors

		for(Category c : cal) {
			c.parentsIds = c.getIdsFromBunch("categories", c.getId());
			if (c.parentsIds != null)
				if (c.parentsIds.get(c.parentsIds.size() - 1) == id) System.out.println(c.getId() + " : " + c.getName());
		}
		Scanner myObj = new Scanner(System.in);
		return myObj.nextInt();
	}

	public static Category menuCreateCategory(ArrayList<Integer> parentsIdsToAdd) {
		String catName = Manager.ask("nom de catégorie : ");
		parentsIds = parentsIdsToAdd;
		return new Category(catName, parentsIds);
	}
	
	@Override
	public String toString() {
		return "id : " + id + ". Ids parents : " + parentsIds + " nom de la category : " + name + "\n";
	}
}
