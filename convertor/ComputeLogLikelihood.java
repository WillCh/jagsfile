import java.io.*;
import java.util.*;

public class ComputeLogLikelihood {
	HashMap<Integer, Integer> state = new HashMap<Integer, Integer>();
	int n_question;
	int n_student = 6;
	int m = 1;
	int n_sample = 20;
	HashMap<Integer, ArrayList<Integer>> map_parents_id = new HashMap<Integer, ArrayList<Integer>>(); // map from s_id -> parents_id
	HashMap<Integer, ArrayList<Double>> map_id_prob = new HashMap<Integer, ArrayList<Double>>();
	//ArrayList<ArrayList<Integer>> sdata = new ArrayList<ArrayList<Integer>>();
	double[][][] prob;		// double[n_sample][n_question][cpt index]
	int[][][] sdata; 	// int[n_sample][n_student][n_question]
	public void readInDag (String fileName) {
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
	   		// process the line.
				String[] line_split = line.split("	");
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				for (int dst = 0; dst < line_split.length; dst++) {
					if (Float.parseFloat(line_split[dst]) == 1.0) {
	    				tmp.add(dst);
					}
				}
				map_parents_id.put(i, tmp);
				i++;
			}
			n_question = i;
		} catch (FileNotFoundException e) {
			System.err.println("Caught FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
		
		
	}

	public void readInState (String fileName) {
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
	   		// process the line.
				int size = Math.round(Float.parseFloat(line));
				state.put(i, size);
				i++;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Caught FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
		
	}

	public void readInSampleData (String fileName) {
		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			// double[][][] prob;		// double[n_sample][n_question][cpt index]
			// int[][][] sdata; 	// int[n_sample][n_student][n_question with m]
			sdata = new int[n_sample][n_student][n_question*m];
			String line;			
			for (int j = 0; j < m; j++) {
				for (int i = 0; i < n_question; i++) {
					int q_id = j*n_question + i;
					for (int student = 0; student < n_student; student++) {
						for (int k = 0; k < n_sample; k++) {
							line = br.readLine();
							if (line == null) System.exit(1);
							String[] tmp = line.split("  ");
							sdata[k][student][q_id] = Integer.parseInt(tmp[1]);
						}
					}
				}
			}

			// read the prob table
			prob = new double[n_sample][n_question][];
			for (int i = 0; i < n_question; i++) {
				ArrayList<Integer> parents_list = map_parents_id.get(i);
				int tot_cpt_size = state.get(i);
				for (Integer parents_id : parents_list) {
					tot_cpt_size *= state.get(parents_id);
				}
				// init the prob
				for (int k = 0; k < n_sample; k++) {
					prob[k][i] = new double[tot_cpt_size];
				}
				for (int cpt_id = 0; cpt_id < tot_cpt_size; cpt_id++) {
					for (int k = 0; k < n_sample; k++) {
						line = br.readLine();
						if (line == null) System.exit(1);
						String[] tmp = line.split("  ");
						prob[k][i][cpt_id] = Double.parseDouble(tmp[1]);
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Caught FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}
	} 

	public void printAvgProb () {
		// double[][][] prob;		// double[n_sample][n_question][cpt index]
		double[][] avg_prob = new double[n_question][];
		for (int i = 0; i < n_question; i++) {
			int cpt_size = prob[0][i].length;
			avg_prob[i] = new double[cpt_size];
			for (int cpt_id = 0; cpt_id < cpt_size; cpt_id++) {
				double avg_tmp = 0.0;
				for (int k = 0; k < n_sample; k++) {
					avg_tmp += prob[k][i][cpt_id];
				}
				avg_prob[i][cpt_id] = avg_tmp / (double)n_sample;
				System.out.print(avg_prob[i][cpt_id] + "; ");
			}
			System.out.println();
		}
		// print the rest

	}

	// sdata = new int[n_sample][n_student][n_question*m];
	// double[][][] prob;		// double[n_sample][n_question][cpt index]
	public double computeloglikelihood (int sample_id, int student_id) {
		double res = 0.0;
		for (int j = 0; j < m; j++) {
			for (int i = 0; i < n_question; i++) {
				int q_id = i + j * n_question;
				//System.out.println(i + "--" + j);
				ArrayList<Integer> parents_list = map_parents_id.get(i);
				// get the shift array
				int[] shift = new int[parents_list.size() + 1];
				int shift_id = 0;
				int cum = 1;
				//System.out.println("print the shift");
				for (Integer parents_id : parents_list) {
					shift[shift_id] = cum;
					//System.out.print(cum + "-" + parents_id + ", ");
					shift_id++;
					cum *= state.get(parents_id); 
				}
				shift[shift_id] = cum;
				//System.out.print(cum);
				//System.out.println();
				int cpt_id = 0;
				shift_id = 0;
				for (Integer parents_id : parents_list) {
					cpt_id += (sdata[sample_id][student_id][parents_id + j * n_question] - 1) * shift[shift_id];
					shift_id++;
				}
				cpt_id += (sdata[sample_id][student_id][q_id] - 1) * shift[shift_id];
				//System.out.println("shift: " + cpt_id);
				// get the cpt value
				res += Math.log(prob[sample_id][i][cpt_id]);
			}
		}
		return res / (double)m;
	}

	public double computeloglikelihood (int sample_id) {
		double res = 0.0;

		for (int student = 0; student < n_student; student++) {
			res += computeloglikelihood(sample_id, student);
		}
		return res / (double)n_student;
	}

	public static void main(String[] args) {
		String dagFile = args[0];
		String stateFile = args[1];
		String sampleFile = args[2];
		ComputeLogLikelihood cmp = new ComputeLogLikelihood();
		cmp.m = Integer.parseInt(args[3]);
		cmp.n_student = Integer.parseInt(args[4]);
		cmp.n_sample = Integer.parseInt(args[5]);
		
		cmp.readInDag(dagFile);
		cmp.readInState(stateFile);
		cmp.readInSampleData(sampleFile);
		cmp.printAvgProb();
		for (int k = 0; k < cmp.n_sample; k++) {
			double loglike = cmp.computeloglikelihood(k);
			System.out.println("the " + k + " loglike = " + loglike);
		}
	}

}