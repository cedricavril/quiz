package quiz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public interface FileManager {

	static boolean openOrCreateFile(String nomFichier) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(nomFichier));
		} catch (FileNotFoundException e) {
			System.out.println("Erreur d'entrée/sortie ou fichier non trouvé. Création de ce dernier.");
	
			try {
				FileWriter f = new FileWriter(nomFichier, false);
	
				f.close();
				System.out.println("fichier " + nomFichier + " créé\n");
				return true;
	
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
				System.out.println("Erreur de création de fichier");
				return false;
			}
		}
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
}
