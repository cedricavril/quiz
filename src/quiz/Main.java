package quiz;

import java.time.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		QA.NOM_FICHIER 			= "qas";
		Category.NOM_FICHIER 	= "categories"; 
		Author.NOM_FICHIER		= "authors";

		Category currentCat;
		String currentCatName = "";
		Author currentAuthor;
		Scanner myObj = new Scanner(System.in);

// commenter ces tests pour garder le contenu des fichiers par défaut
//		testsAuthor();
//		testsCategory();
//		testsQA();

		int i = mainMenu();

		if (i == 1) {
		    Manager.clrscr();
			// author menu
			Author.showAuthors(Author.loadAuthors());
			int askedAuthorId = Integer.parseInt(Manager.ask("Auteur ? 0 pour tous"));
			currentAuthor = (askedAuthorId == 0) ? null : Author.loadAuthor(askedAuthorId);
			System.out.println("auteur choisi : " + currentAuthor);

			// category menu
			System.out.println("Catégorie ? 0 pour toutes ou sélection puis 0 pour conserver la catégorie choisie");
			ArrayList<Category> cal = Category.showMainCategories("");
			ArrayList<Integer> parentsIdsToAdd = new ArrayList<Integer>();
			int id = 1, idCategory = 0;
			String nameCat = "";

			// TODO filter by author but here
			// TODO filter by date
			for(id = myObj.nextInt(); id > 0; id = Category.menuSubCategories(cal, id)) {
				System.out.println("Catégorie ? 0 pour conserver la catégorie choisie");
				nameCat = Manager.loadItemFromBunch("categories", id);
				System.out.println("catégories de " + nameCat + " : ");
				parentsIdsToAdd.add(id);
				idCategory = id;
			}

			if (id == 0) {
			    currentCat = new Category(nameCat, parentsIdsToAdd);
			    currentCatName = (nameCat == "") ? "toutes" : currentCat.getName();
			    System.out.println("catégorie choisie : " + currentCatName);
			}

// filtering by chosen category and chosen author
			ArrayList<QA> qas = QA.loadQAs();

			int qasCount = qas.size();
			if (idCategory != 0) {
				for(int index = 0; index < qasCount; index++) {
					if (!currentCatName.equals("toutes") & qas.get(index).getIdCategory() != idCategory) {
						qas.remove(index);
						index--;
						qasCount--;
					} else if (currentAuthor != null & qas.get(index).getIdAuthor() != askedAuthorId) {
						qas.remove(index);
						index--;
						qasCount--;
					}
				}
			}

// TODO ask to go to previous questions for updating them
			for(int index = 0; index < qas.size(); index++) {
				System.out.println("Quiz has started. " + qas.size() + " questions");
				System.out.println(qas.get(index).getQ());
				System.out.println();
				for(String a : qas.get(index).getA())
					System.out.println(a);
				String rep = myObj.next();
				if (rep.equals(qas.get(index).getNum_a()))
					qas.remove(index--);
			}

			System.out.println("result score : " + (float)(qasCount - qas.size()) * 100 / qasCount + "%");
			System.out.println("wrong answers : " + qas);
			
		}

		if (i == 2) {
			Author a;
			Category cat = null;

			// author menu
		    Manager.clrscr();
			Author.showAuthors(Author.loadAuthors());
			int askedAuthorId = Integer.parseInt(Manager.ask("Auteur ? 0 pour nouveau"));
			currentAuthor = (askedAuthorId == 0) ? null : Author.loadAuthor(askedAuthorId);
			System.out.println("auteur choisi : " + currentAuthor);

			// same deal, Cf. saveItemsFromGroup comparing before creating, but mem optimisation
			if(askedAuthorId == 0) {
				a = Author.createAuthor();
				System.out.println("Auteur créé : " + a);
				a.saveAuthor();
			} else {
				a = Author.loadAuthor(askedAuthorId);
				System.out.println("auteur choisi : " + a);
			}

			// category menu
			ArrayList<Category> cal = Category.showMainCategories("");
			ArrayList<Integer> parentsIdsToAdd = new ArrayList<Integer>();
			int id = 0;
			String 	nameCat = "",
					oldCat = "";

			do {
				System.out.println("Catégorie ? 0 pour conserver la catégorie choisie, -1 pour en créer une à partir de la catégorie choisie");
				oldCat = nameCat ;
				nameCat = Manager.loadItemFromBunch("categories", id);
				if (nameCat != null) System.out.println("catégories de " + nameCat + " : ");
				else oldCat = "";
				id = Category.menuSubCategories(cal, id);
				if (id>0) parentsIdsToAdd.add(id);
				else if (id == 0 && parentsIdsToAdd.isEmpty()) System.out.println("la catégorie doit être renseignée");
				else if (id == -1) oldCat = nameCat;
			} while(id < -1 || id > 0 || id == 0 && parentsIdsToAdd.isEmpty());

			if (id == 0) {
				cat = new Category(nameCat);
				System.out.println("cat choisie : " + cat);
			} else {
				// crée une catégorie si non existante
				cat = Category.menuCreateCategory(parentsIdsToAdd);
				System.out.println("cat créée choisie : " + cat);
			}

			if (oldCat == null) oldCat = "<aucune catégorie>";
			if (oldCat.equals("")) oldCat = "<aucune catégorie>";
			if (!parentsIdsToAdd.isEmpty()) System.out.println("Catégorie courante fille de : " + oldCat + " du nom de " + cat.getName());
			else System.out.println("Catégorie principale courante : " + cat.getName());

			cat.saveCategory();
			System.out.println("catégorie courante : " + cat.getName()); 
			// \menu catégories

			// menu qas
			
			if (Integer.parseInt(Manager.ask("1 : créer un q/a. \n0 : quitter.")) != 0) {
				System.out.println("Question-Answer.");
				String q = Manager.convArrayListOfStringsToString(Manager.askText("Question : "));
				ArrayList<String> as = new ArrayList<String>(Manager.askText("Réponses : "));
				String 	detail = Manager.convArrayListOfStringsToString(Manager.askText("Détails : "));
				String num_a = Manager.ask("numéro de la bonne réponse : ");

				int qaId = Manager.getIdFromMainFile(QA.NOM_FICHIER, q);

// TODO mettre des *** pour décorer et éviter des textes d'une ligne.				
				QA qa = new QA(qaId, q, detail, as, num_a, LocalDate.now(), a.getId(), cat.getId());
				qa.saveQA();

				System.out.println("QA créée : " + qa);
				
				ArrayList<QA> qas = QA.loadQAs();
				System.out.println("les QAs : ");
				for(QA qaToShow : qas) {
					System.out.println(qaToShow);
				}

			}

						
			// \menu qas
			
		}
		System.out.println("fin du programme.");
	}

	public static int mainMenu() {
		System.out.println("Quiz");
		int i;
		for(i = 0; i < 1 || i > 3;) {
			Manager.clrscr();
			System.out.println("1 : Quiz");
			System.out.println("2 : Administration");
			System.out.println("3 : Quitter");
			Scanner myObj = new Scanner(System.in);
			i = myObj.nextInt();
		}
		return i;
	}
	
	public static void testsQA() {

		String q = "Quel est le nombre de neurones dans le soit disant cerveau de Béatrice Fichera ?\n(la réponse est dans la question)";
		String details = "Détails 0\nretour à la ligne";
		ArrayList<String> a = new ArrayList<String>(Arrays.asList("1", "soit disant 2")); 
		QA qa = new QA(3, q, details, a, "2", LocalDate.now().minusDays(1), 2, 18);
		String q2 = "Question 2 ?\n(parenthèses)";
		String details2 = "Détails 2\nretour à la ligne 2";
		ArrayList<String> a2 = new ArrayList<String>(Arrays.asList("deux", "trois")); 
		QA qa2 = new QA(4, q2, details2, a2, "1", LocalDate.now(), 3, 19);
		qa.saveQA();
		qa2.saveQA();

		q = "question 1\nretour à la ligne";
		details = "Détails 1\nretour à la ligne";
		a = new ArrayList<String>(Arrays.asList("réponse 1", "réponse 2")); 
		qa = new QA(5, q, details, a, "2", LocalDate.now().minusDays(1), 2, 18);
		System.out.println("id de q : " + QA.getId(q));

		ArrayList<QA> qas = QA.loadQAs();

		System.out.println("les QAs : ");
		for(QA qaToShow : qas) {
			System.out.println(qaToShow);
		}
		
	}
	public static void testsAuthor() {
		Author a = new Author("nom test", "prénom test", LocalDate.now());
		a.saveAuthor();
		Author b = new Author("nom test b", "prénom test b", LocalDate.of(2021, Month.APRIL, 2)); 
		b.saveAuthor();
	}

	public static void testsCategory() {
		Category cat;
		ArrayList<Integer> parentsIds = new  ArrayList<Integer>();

		cat = new Category("nom cat 1");
		cat.saveCategory();
		parentsIds.clear();
		parentsIds.add(cat.getId());
		cat = new Category("nom cat 1.1", parentsIds);
		cat.saveCategory();
		parentsIds.add(cat.getId());
		cat = new Category("nom cat 1.1.1", parentsIds);
		cat.saveCategory();
		cat = new Category("nom cat 1.1.2", parentsIds);
		cat.saveCategory();
		cat = new Category("nom cat 1.1.3", parentsIds);
		cat.saveCategory();
		cat = new Category("nom cat 1.1.4", parentsIds);
		cat.saveCategory();

		cat = new Category("nom cat 1");
		cat.saveCategory();
		parentsIds.clear();
		parentsIds.add(cat.getId());
		cat = new Category("nom cat 1.2", parentsIds);
		cat.saveCategory();
		parentsIds.add(cat.getId());
		cat = new Category("nom cat 1.2.1", parentsIds);
		cat.saveCategory();
		cat = new Category("nom cat 1.2.2", parentsIds);
		cat.saveCategory();
		cat = new Category("nom cat 1.2.3", parentsIds);
		cat.saveCategory();
		cat = new Category("nom cat 1.2.4", parentsIds);
		cat.saveCategory();

		System.out.println("cat test : " + cat);
	}	
}