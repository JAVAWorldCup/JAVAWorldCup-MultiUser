import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

public class WordList {
	private Vector<String> wordVector = new Vector<String>();
	private Vector<String> badVector = new Vector<String>();
	private Vector<String> goodVector = new Vector<String>();
	
	public WordList() {
		try {
			Scanner scanner = new Scanner(new FileReader("static/words.txt"));
			Scanner scannerGood = new Scanner(new FileReader("static/good.txt"));
			Scanner scannerBad = new Scanner(new FileReader("static/bad.txt"));
			while(scanner.hasNext()) { // 일반 단어
				String word = scanner.nextLine();
				wordVector.add(word);
			}
			while(scannerGood.hasNext()) { // 아이템(penalty kick)
				String word = scannerGood.nextLine();
				goodVector.add(word);
			}
			while(scannerBad.hasNext()) { // 아이템(red card, yellow card)
				String word = scannerBad.nextLine();
				badVector.add(word);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	// 무작위 단어 하나 가져오기
	public String getWord() {
		int index = (int)(Math.random()*wordVector.size());
		return wordVector.get(index);
	}
	// 아이템(penalty kick) 단어 무작위로 가져오기
	public String getGoodWord() {
		int index = (int)(Math.random()*goodVector.size());
		return goodVector.get(index);
	}
	// 아이템(red card, yellow card) 단어 무작위로 가져오기
	public String getBadWord() {
		int index = (int)(Math.random()*badVector.size());
		return badVector.get(index);
	}
}
