import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Vector;

public class RankingList {
	private Vector<String> nameVector = new Vector<String>(); // 모든 이름
	private Vector<String> goalVector = new Vector<String>(); // 모든 골 수
	private Vector<String> rankingNameVector = new Vector<String>(); // top 5 이름
	private Vector<String> rankingGoalVector = new Vector<String>(); // top 5 골 수
	
	public RankingList(int level) {
		try {
			if(level == 0) { // easy
				Scanner scannerEasy = new Scanner(new FileReader("static/easy.txt"));
				while(scannerEasy.hasNext()) {
					String name = scannerEasy.nextLine();
					String goal = scannerEasy.nextLine();
					nameVector.add(name);
					goalVector.add(goal);
				}
			}
			else if(level == 1) { //normal
				Scanner scannerNormal = new Scanner(new FileReader("static/normal.txt"));
				while(scannerNormal.hasNext()) {
					String name = scannerNormal.nextLine();
					String goal = scannerNormal.nextLine();
					nameVector.add(name);
					goalVector.add(goal);
				}
			}
			else if(level == 2) { //hard
				Scanner scannerHard = new Scanner(new FileReader("static/hard.txt"));
				while(scannerHard.hasNext()) {
					String name = scannerHard.nextLine();
					String goal = scannerHard.nextLine();
					nameVector.add(name);
					goalVector.add(goal);
				}
			}
			
			sort();
			
		} catch (FileNotFoundException e) {}
	}
	// 정렬(top 5)
	private void sort() {
		int count =0;
		while(true) {
			if(count == 5) // top 5만 뽑기 위함 
				break;
			int index=0; // 최대 골 수의 인덱스
			int max = 0; // 골 수 최대 값
			for(int i=0; i<goalVector.size(); i++) {
				int goal = Integer.parseInt(goalVector.get(i));
				if(max < goal) {
					max = goal;
					index = i;
				}
			}
			rankingNameVector.add(nameVector.get(index)); // top 5 Vector에 add
			rankingGoalVector.add(goalVector.get(index)); // top 5 Vector에 add
			goalVector.remove(index);
			nameVector.remove(index);
			count++;
		}
	}
	
	public String getName(int index) { return rankingNameVector.get(index);}
	public String getGoal(int index) { return rankingGoalVector.get(index);}
}
