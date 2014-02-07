import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;

public class ArenaSimulator {

//	private static int timesRun;
	private static List<Cards> listDeck = new ArrayList<>();
	private static List<Cards> listChoices = new ArrayList<>();
	private static Cards[] cards;
//	private static StringBuilder format = new StringBuilder();
//	private static StringBuilder deckContent = new StringBuilder();

	static int uncommon_total = 0;
	static int common_total = 0;
	static int rare_total = 0;
	static int epic_total = 0;
	static int legendary_total = 0;

	public static void main(String[] args) {

		// parse json with gson
		setupCards();

		System.out.println("Enter number of arena runs to simulate: ");
		Scanner scanner = new Scanner(System.in);
		int numOfRuns = scanner.nextInt();
		scanner.close();

		System.out.println("Calculating...");

		final long startTime = System.currentTimeMillis();
		
		// run simulations
		for (int i = 0; i < numOfRuns; i++) {
			listDeck.clear();

			// pick 30 cards
			for (int j = 0; j < 30; j++) {
				pickRandomCard(Classes.SHAMAN);
			}

			calcCardTypes();
		}
		
		final long endTime = System.currentTimeMillis();

		SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
		String format = s.format(new Date());
		String totalDeckContent = "Total of all " + numOfRuns
				+ " runs -- Date: " + format + " Executed in: " + (endTime - startTime) / 1000.0f + " Seconds" + "\nUncommon: " + uncommon_total
				+ "  Common: " + common_total + "  Rare: " + rare_total
				+ "  Epic: " + epic_total + "  Legendary: " + legendary_total
				+ "\n\n";

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter("myfile.txt", true)))) {
			out.println(totalDeckContent);
			out.close();
			System.out.println("Success");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fail");
		}

	}

	private static void pickRandomCard(Classes className) {
		double random = Math.random();
		int size = listDeck.size();

		/*
		 * ************ LEGENDARY ************ ~3% chance if either first or
		 * last choice of 3. Otherwise, ~1% chance
		 */
		if (random >= 0.991
				|| ((size == 0 || size == 29) && random >= 0.97)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 5) {
					listChoices.add(card);
				}
			}
		}
		/*
		 * ************ EPIC ************ ~10% chance if either first or last
		 * choice of 3.
		 * 
		 * Otherwise, ~4% chance
		 */
		else if (random >= 0.965
				|| ((size == 0 || size == 29) && random >= 0.90)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 4) {
					listChoices.add(card);
				}
			}
		}
		/*
		 * ************ RARE ************ ~100% chance if either first or last
		 * choice of 3 (unless a higher rarity is chosen).
		 * 
		 * Otherwise, ~8% chance
		 */
		else if (random >= 0.92
				|| ((size == 0 || size == 29) && random >= 0)) {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& card.getQuality().intValue() == 3) {
					listChoices.add(card);
				}
			}
		} else {
			for (Cards card : cards) {
				if (((card.getClasss() != null && card.getClasss().intValue() == className
						.getValue()) || card.getClasss() == null)
						&& (card.getQuality().intValue() == 1 || card
								.getQuality().intValue() == 0)) {
					listChoices.add(card);
				}
			}
		}

		Collections.shuffle(listChoices);
		int soRandom = (int) Math.random() * 3;
		listDeck.add(listChoices.get(soRandom));

	}

	private static void calcCardTypes() {
//
//		int uncommon = 0;
//		int common = 0;
//		int rare = 0;
//		int epic = 0;
//		int legendary = 0;

		for (Cards card : listDeck) {
			switch (card.getQuality().intValue()) {
			case 0:
//				uncommon++;
				uncommon_total++;
				break;
			case 1:
//				common++;
				common_total++;
				break;
			case 3:
//				rare++;
				rare_total++;
				break;
			case 4:
//				epic++;
				epic_total++;
				break;
			case 5:
//				legendary++;
				legendary_total++;
				break;
			}
		}
		// SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
		// format.append(s.format(new Date()));
		// deckContent.append("Run #" + timesRun + " Date: " + format.toString()
		// + "\nUncommon: " + uncommon + "  Common: " + common
		// + "  Rare: " + rare + "  Epic: " + epic + "  Legendary: "
		// + legendary + "\n\n");
		//
		// try(PrintWriter out = new PrintWriter(new BufferedWriter(new
		// FileWriter("myfile.txt", true)))) {
		// out.println(deckContent.toString());
		// out.flush();
		// out.close();
		// }catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	private static void setupCards() {

		Gson gson = new Gson();

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(
					"C:\\Users\\JT\\workspace\\ArenaSimulator\\bin\\cards.json");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(fis,
					"UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// The json String from the file
		String jsonString = writer.toString();

		// Set our pojo from the GSON data
		cards = gson.fromJson(jsonString, Cards[].class);
	}

}
