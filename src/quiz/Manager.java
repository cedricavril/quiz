package quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.*;

// TODO : virer les level. Une branche max.
public interface Manager extends FileManager {

		static int getIdFromMainFile(String nomFichier, String q) {
			BufferedReader 					reader;
			String 							currentLine;
			boolean 						sameFirstItem;
			int 							i = 0;
			String[] 						sa = q.split("\n");
			int 							id, maxId = 0;
			
			try {
				reader = new BufferedReader(new FileReader(nomFichier));
				mainLoop: while(((currentLine = reader.readLine()) != null)) {
					Pattern matchingId = Pattern.compile("^-(\\d+)(-\\d+)*( .+)");
					Matcher m = matchingId.matcher(currentLine);

					if(m.matches()){
						String lineCompared = m.group(3);
						id = Integer.parseInt(m.group(1));
						if (maxId < id) maxId = id;
						do {
							sameFirstItem = lineCompared.equals(" " + sa[i]);
							if (!sameFirstItem) continue mainLoop;
							i++;
						}
						while((( lineCompared = reader.readLine()) != null) && (i < sa.length) && sameFirstItem);
						if (sameFirstItem && (i == sa.length)) {
							reader.close();
							return id;
						}
						i = 0;
					};
				}
				reader.close();
			} catch (IOException e) {
				System.out.println("Erreur d'entrée/sortie ou fichier non trouvé pour loadItemsFromGroup.");
				e.printStackTrace();
			}

			return ++maxId;
		}
			

		static ArrayList<String> convStringToArrayListOfStrings(String s) {
			ArrayList<String> res = new ArrayList<String>();
			res.add(s);
			return res;
		}

		static String convArrayListOfStringsToString(ArrayList<String> sal) {
			String res = "";
			for(String s : sal ) {
				res += s + "\n";
			}
			res = res.substring(0, res.length() - 1);
			return res;
		}

// level <= 1

// TODO le delete devra décrémenter tous les ids des children. Pareil pour les create ou add qui devront incrémenter.
	default boolean delItems(String nomFichier, int id, int id2, String...propagationFiles) {

		File inputFile = new File(nomFichier);
		File tempFile = new File("tempFile.txt");
		String itemHeadline = "" + id;
		String currentLine;
		String nextItemStarts = "-";

		if (id2 == 0) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
				while((currentLine = reader.readLine()) != null) {
				    if(currentLine.equals(itemHeadline)) {
					    while((currentLine = reader.readLine()) != null && currentLine.startsWith(nextItemStarts)) {
					    }
				    }
				    writer.write(currentLine + System.getProperty("line.separator"));
				}
				writer.close();
				reader.close();
				return tempFile.renameTo(inputFile);
			} catch (IOException e) {
				System.out.println("Erreur d'entrée/sortie ou fichier " + nomFichier + "non trouvé.");
			}
		}
		return true;
	}

// à virer si ne sert pas
	default Map<String, ArrayList<String>> loadItems(String nomFichier) {

		File 							inputFile = new File(nomFichier);
		String 							currentLine,
										id = "",
										subItemStarting = "-";
		ArrayList<String> 				subItems = new ArrayList<>();
		Map<String, ArrayList<String>> 	resultItems = new HashMap<>();
		BufferedReader 					reader;

		try {
			reader = new BufferedReader(new FileReader(inputFile));
			do {
			    while((currentLine = reader.readLine()) != null && currentLine.startsWith(subItemStarting))
	    			subItems.add(currentLine.substring(currentLine.indexOf(' ') + 1));
			    	if (id != "") {
						resultItems.put(id, (ArrayList<String>)subItems.clone());
						subItems.clear();
			    	}
			    	id = currentLine;
				} while(currentLine != null);

			} catch (IOException e) {
			System.out.println("Erreur d'entrée/sortie ou fichier " + nomFichier + "non trouvé.");
		}

		
		return resultItems;
	}


	default ArrayList<String> loadItemsNameList(String nomFichier) {
		File 							inputFile = new File(nomFichier);
		String 							currentLine,
										id = "",
										itemStarting = "-";
		ArrayList<String> 				items = new ArrayList<>();
		BufferedReader 					reader;

		try {
			reader = new BufferedReader(new FileReader(inputFile));
			while((currentLine = reader.readLine()) != null && currentLine.startsWith(itemStarting))
				items.add(currentLine.replaceFirst("^(-\\d*)*-(\\d*) (.*)", "$2 : $3"));
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return items;
	}
	
	default int getIdFromBunch(String nomFichier, String nomItem) {
		File 			inputFile = new File(nomFichier);
		String 			currentLine;
		int 			max = 0, 
						res = 0;
		BufferedReader 	reader;
		try {
			
			reader = new BufferedReader(new FileReader(inputFile));
			while((currentLine = reader.readLine()) != null) {
	
				Pattern pToFind = Pattern.compile(	"^[-||\\d]*-(\\d+) " + nomItem);
				Matcher mToFind = pToFind.matcher(currentLine);
				Pattern p = Pattern.compile(		"^[-||\\d]*-(\\d*) .*");
				Matcher m = p.matcher(currentLine);

				m.matches();
				res = Integer.parseInt(m.group(1));
				if (max < res) max = res;
				if (mToFind.matches()) {
					res = Integer.parseInt(mToFind.group(1));
					reader.close();
					return res;
				}
			}
		} catch (IOException e) {
			System.out.println("Erreur 3 - d'entrée/sortie ou fichier non trouvé.");
			e.printStackTrace();
		}
		return (max + 1);
	}

	default ArrayList<Integer> getIdsFromBunch(String nomFichier, int id) {
		// we assume nomFichier exists
				File 				inputFile = new File(nomFichier);
				String 				currentLine, 
									resString;
				ArrayList<Integer>	res = new ArrayList<>();
				BufferedReader 		reader;

				try {
					reader = new BufferedReader(new FileReader(inputFile));

					while((currentLine = reader.readLine()) != null) {
						Pattern pToFind = Pattern.compile("^((-\\d+)+)-" + id + " .+");
						Matcher mToFind = pToFind.matcher(currentLine);

						if (mToFind.matches() && mToFind.group(1) != null) {
							reader.close();
							resString = mToFind.group(1);
							for(String s : resString.split("-")) {
								if (s.equals("")) continue;
								res.add(Integer.parseInt(s));
							}
							return res;
						}
					}
				} catch (IOException e) {
					System.out.println("Erreur 3 - d'entrée/sortie ou fichier non trouvé.");
					e.printStackTrace();
				}
		
		return null;
	}

	default ArrayList<Integer> getFirstChildrenIdsFromId(String nomFichier, int id) {

		// we assume nomFichier exists
		File 				inputFile = new File(nomFichier);
		String 				currentLine, 
							resString;
		ArrayList<Integer>	res = new ArrayList<>();
		BufferedReader 		reader;

		try {
			reader = new BufferedReader(new FileReader(inputFile));

			while((currentLine = reader.readLine()) != null) {
				Pattern pToFind = Pattern.compile("^((-\\d+)+)-" + id + " .+");
				Matcher mToFind = pToFind.matcher(currentLine);

				if (mToFind.matches() && mToFind.group(1) != null) {
					reader.close();
					resString = mToFind.group(1);
					for(String s : resString.split("-")) {
						if (s.equals("")) continue;
						res.add(Integer.parseInt(s));
					}
					return res;
				}
			}
		} catch (IOException e) {
			System.out.println("Erreur 3 - d'entrée/sortie ou fichier non trouvé.");
			e.printStackTrace();
		}

		return null;
		
	}
		
	static String loadItemFromBunch(String nomFichier, int id) {
		// we assume nomFichier exists
		File 			inputFile = new File(nomFichier);
		String 			currentLine;
		Boolean 		b = false;
		BufferedReader	reader;

		try {
			reader = new BufferedReader(new FileReader(inputFile));

			while((currentLine = reader.readLine()) != null) {
				Pattern pToFind = Pattern.compile(".*-" + id + " (.*)");
				Matcher mToFind = pToFind.matcher(currentLine);

				b = mToFind.matches();
				if (b) {
					reader.close();
					return mToFind.group(1);
				}
			}
		} catch (IOException e) {
			System.out.println("Erreur 3 - d'entrée/sortie ou fichier non trouvé.");
			e.printStackTrace();
		}

		return null;
	}

	public static Map<Integer, ArrayList<String>> loadItemsFromGroup(String nomFichier, String firstContent) {
		// will check if firstcontent (question) exists and will return the wole matching item 
				BufferedReader 					reader;
				String 							currentLine;
				int 							i = 1, 
												j = 0;
				String[] 						sa = firstContent.split("\n");
				ArrayList<String> 				subItemToPut = new ArrayList<String>();
				Map<Integer, ArrayList<String>>	item = new HashMap<>();

				Pattern matchingNew;
				Matcher mNew;
				Pattern matchingNext;
				Matcher mNext;
				Pattern matchingSub;
				Matcher mSub;
				Pattern matchingSubItems;
				Matcher mSubItems;
				Pattern matchingNextSubItem;
				Matcher mNextSubItem;
				String linesToPut = "";
				matchingNew = Pattern.compile("^-(\\d+) (.*)");
				
				try {
					reader = new BufferedReader(new FileReader(nomFichier));
					mainLoop: while((currentLine = reader.readLine()) != null) {
						j = 0;
						Matcher m = matchingNew.matcher(currentLine);
						m.matches();
						if (!m.matches()) continue mainLoop;
						
						linesToPut = m.group(2);
						
						if (m.matches() && m.group(2).equals(sa[j])) {
							while((currentLine = reader.readLine()) != null) {
								matchingNext = Pattern.compile("^ (\\S.*)");
								mNext = matchingNext.matcher(" " + currentLine.trim());
								j++;
								if (mNext.matches() && mNext.group(1).trim().equals(sa[j].trim())) {
									linesToPut += "\n" + mNext.group(1);
									if (j == sa.length - 1) {
										item.put(0, convStringToArrayListOfStrings(m.group(1)));
										break mainLoop;
									}
								} else {
									linesToPut = "";
									break;
								}
							}
						} else linesToPut = "";
						if (j >= sa.length) break;
					}

					if (currentLine == null) {
						reader.close();
						return new HashMap<>();
					}

					 while((currentLine = reader.readLine()) != null) {
						matchingSub = Pattern.compile("^- (\\S.*)");
						mSub = matchingSub.matcher(currentLine);
						matchingSubItems = Pattern.compile("^-  (.*)");
						mSubItems = matchingSubItems.matcher(currentLine);
						matchingNext = Pattern.compile("^ (\\S.*)");
						mNext = matchingNext.matcher(currentLine);
						matchingNextSubItem = Pattern.compile("^  (.*)");
						mNextSubItem = matchingNextSubItem.matcher(currentLine);

// peut être à simplifier aisément

						matchingNew = Pattern.compile("^-(\\d+) (.*)");
						mNew = matchingNew.matcher(currentLine);
						if (mNew.matches()) {
							if (linesToPut != "") {
								item.put(i, convStringToArrayListOfStrings(linesToPut));
								linesToPut = "";
							}
							if (!subItemToPut.isEmpty()) {
								item.put(i, subItemToPut);
								subItemToPut = new ArrayList<String>();
							}
							return item;
						}

						if (mSub.matches()) {
							if (linesToPut != "") {
								item.put(i, convStringToArrayListOfStrings(linesToPut));
								linesToPut = "";
							}
							if (!subItemToPut.isEmpty()) {
								item.put(i, subItemToPut);
								subItemToPut = new ArrayList<String>();
							}
							i++;
							linesToPut = mSub.group(1);
						}
						if (mSubItems.matches()) {
							if (linesToPut != "") {
								item.put(i, convStringToArrayListOfStrings(linesToPut));
								linesToPut = "";
							}
							if (!subItemToPut.isEmpty()) {
								item.put(i, subItemToPut);
								subItemToPut = new ArrayList<String>();
							}
							i++;
							subItemToPut.add(mSubItems.group(1));
						}
						if (mNext.matches()) {
							linesToPut += "\n" + mNext.group(1);
							if (++j < sa.length) {
								if (!mNext.group(1).equals(sa[j])) return new HashMap<>();
							}
						}
						if (mNextSubItem.matches())
							subItemToPut.add(mNextSubItem.group(1));
					}
					if (linesToPut != "") item.put(i, convStringToArrayListOfStrings(linesToPut));
					if (!subItemToPut.isEmpty()) item.put(i, subItemToPut);

					reader.close();
				} catch (IOException e) {
					System.out.println("Erreur d'entrée/sortie ou fichier non trouvé pour loadItemsFromGroup.");
					e.printStackTrace();
				}
				
				return item;
			}

	public static String loadFirstContent(String nomFichier, int id) {
		BufferedReader 	reader;
		String			item = "";

		String currentLine;
		
		try {
			reader = new BufferedReader(new FileReader(nomFichier));
			while(((currentLine = reader.readLine()) != null)) {
				Pattern p = Pattern.compile("^(-\\d*)*-" + id + " (.+)");
				Matcher m = p.matcher(currentLine);
				if (m.matches()) {
					item = m.group(2);
					while(((currentLine = reader.readLine()) != null)) {
						if (!currentLine.startsWith("-")) {
							item += "\n" + currentLine.trim();
						}
						
						else {
							reader.close();
							return item;
						}
					}					
				}
				
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Erreur d'entrée/sortie ou fichier non trouvé pour loadItemsFromGroup.");
			e.printStackTrace();
		}
		return item;
	}
	public static Map<Integer, String> loadFirstContents(String nomFichier) {

		BufferedReader 					reader;
		int								id = 0; 
		String 							currentLine = "", 
										linesToPut = "";
		Map<Integer, String>			item = new HashMap<>();

		try {
			reader = new BufferedReader(new FileReader(nomFichier));
			while(((currentLine = reader.readLine()) != null)) {
				Pattern matchingIds = Pattern.compile("^-(\\d+) (.+)");
				Matcher m = matchingIds.matcher(currentLine);
				if (m.matches()) {
					id = Integer.parseInt(m.group(1));
					linesToPut += m.group(2) + "\n";
					while(((currentLine = reader.readLine()) != null)) {
						m = matchingIds.matcher(currentLine);
// cas particulier (e.g. auteurs)
						if (m.matches()) {
							item.put(id, linesToPut);
							linesToPut = m.group(2) + "\n";
							id = Integer.parseInt(m.group(1));
							while(((currentLine = reader.readLine()) != null)) {
								m = matchingIds.matcher(currentLine);
								if (m.matches()) {
									item.put(id, linesToPut);
									linesToPut = m.group(2) + "\n";
									id = Integer.parseInt(m.group(1));
								} else linesToPut += currentLine + "\n";
							} if (currentLine == null) {
								item.put(id, linesToPut);
								reader.close();
								return item;
							}
						}
						if (currentLine.startsWith("-")) break;
						//linesToPut += currentLine.trim();
						linesToPut += currentLine.trim() + "\n";
					}
					item.put(id, linesToPut);
					linesToPut = "";
				}
			};
			reader.close();
			return item;
		} catch (IOException e) {
			System.out.println("Erreur d'entrée/sortie ou fichier " + nomFichier + " non trouvé pour loadItemsFromGroup.");
			e.printStackTrace();
		}

		return item;
	}

	// upsert - peut être utiliser ça aussi pour author
	default boolean saveItemsFromGroup(String nomFichier, Map<Integer, ArrayList<String>> items, Integer id) {
		FileManager.openOrCreateFile(nomFichier);

		File tempFile = new File("tempFile.txt");
		File inputFile = new File(nomFichier);
		BufferedReader reader;
		BufferedWriter writer;
		
		String currentLine;

		try {
			reader = new BufferedReader(new FileReader(inputFile));
			writer = new BufferedWriter(new FileWriter(tempFile));
			mainLoop: while((currentLine = reader.readLine()) != null) {
				Pattern matchingIds = Pattern.compile("^-(" + id + ") .+");
				Matcher m = matchingIds.matcher(currentLine);

				if(m.matches()){
					while((currentLine = reader.readLine()) != null) {
						Pattern otherIdReached = Pattern.compile("^-(\\d+) .+");
						if (otherIdReached.matcher(currentLine).matches()) {
							writer.write(currentLine + System.getProperty("line.separator"));
							continue mainLoop;
						}
					};
				}
				if (currentLine != null) writer.write(currentLine + System.getProperty("line.separator"));
			}

			// item insertion at the end of the temp file
			// head
			String[] sa = items.get(0).get(0).toString().split("\n");
			for(int i = 0; i < sa.length; i++) {
				if (i == 0) writer.write("-" + id + " " + sa[i] + System.getProperty("line.separator"));
				else writer.write(" " + sa[i] + System.getProperty("line.separator"));
			}

			if (items.size() == 1) {
				reader.close();
				writer.close();
				return tempFile.renameTo(inputFile);
			}

// temp - details, peut être à fusionner avec au dessus du if d'au dessus
			sa = items.get(1).get(0).toString().split("\n");
			for(int i = 0; i < sa.length; i++) {
				if (i == 0) writer.write("-" + " " + sa[i] + System.getProperty("line.separator"));
				else writer.write(" " + sa[i] + System.getProperty("line.separator"));
			}

			// body
			for(int i = 2; i < items.size(); i++) {
				String prefix = "-";
				for (String line : items.get(i)) {
					writer.write(prefix + " " + line + System.getProperty("line.separator"));
					if (prefix == "-") prefix = "";
				}
			}
				
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tempFile.renameTo(inputFile);
	}
	
	// upsert
	default boolean saveSubItem(String nomFichier, String nomItem, ArrayList<Integer> parentsIds, int id) {
		boolean b = false, LineIsSaved = false;
		String savingLine;

		if (parentsIds == null || parentsIds.isEmpty()) savingLine = "-" + id + " " + nomItem;
		else  {
			int max = (parentsIds.size() == 0) ? 0 : Collections.max(parentsIds);
			if (max > id) id = max + 1;
			savingLine = "-" + parentsIds.toString().substring(1, parentsIds.toString().length() - 1) + "-" + id;
			savingLine = savingLine.replaceAll(", ", "-") + " " + nomItem;
		}

		nomItem = " " + nomItem; // so nomItem won't start with a key word such as any digit

		File inputFile = new File(nomFichier);
		File tempFile = new File("tempFile.txt");

		BufferedReader reader;
		FileManager.openOrCreateFile(nomFichier);
		String currentLine;

		try {
			
			reader = new BufferedReader(new FileReader(inputFile));

			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			String oldLine = "";
			while((currentLine = reader.readLine()) != null) {

				// compilation de la regex
				Pattern p = Pattern.compile("^(-\\d*)*-" + id + " (.+)");
				// création d'un moteur de recherche
				Matcher m = p.matcher(currentLine);
				// lancement de la recherche de toutes les occurrences
				b = m.matches();
				if(b){
					writer.write(savingLine + System.getProperty("line.separator"));
					LineIsSaved = true;
			    } else writer.write(currentLine + System.getProperty("line.separator"));
				oldLine = currentLine;
			}
			if (!LineIsSaved) {
				if (!savingLine.equals(oldLine)) writer.write(savingLine + System.getProperty("line.separator"));
			}
			writer.close();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		return tempFile.renameTo(inputFile);
		
	}

	default String getStringFromDate(LocalDate lt) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yy");
		return lt.format(df);
	}

	static LocalDate getDateFromString(String d) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yy");
		return LocalDate.parse(d, df);
	}

// lambda pourrait être possible pour fusionner les 2 ask avec ask(s -> s.equals("")) et ask(s -> !s.equals(""))
	public static String ask(String q) {
		Scanner myObj = new Scanner(System.in);
		System.out.println(q);
		String s = "";
		while(s.equals("")) {
			s = myObj.nextLine(); 
			if (s.equals("")) System.out.println("chaine vide");
		}
		//myObj.close();
		return s;
	}

	public static ArrayList<String> askText(String q) {
		Scanner myObj = new Scanner(System.in);
		System.out.println(q + " entrez une chaine vide pour finir.");
		ArrayList<String> sal = new ArrayList<String>();
		String s = " ";
		do {
			s = myObj.nextLine();
			if (s.equals("")) System.out.println("fin de saisie");
			else sal.add(s);
		} while(!s.equals(""));
		//myObj.close();
		return sal;
	}
	
	
	public static void clrscr() {
// won't work because process is much more complex.
		try {
	        if(System.getProperty("os.name" ).startsWith("Windows" ))
	          Runtime.getRuntime().exec("cls" );
	        else {
	          Runtime.getRuntime().exec("clear" );

	        }
	      } catch(Exception excpt) {
	        for(int doh=0;doh<100;doh++)
	          System.out.println();
	      }
	}
	
}
