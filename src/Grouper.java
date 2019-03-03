/**
 * 
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import objects.Degree;
import objects.DA;
import objects.Cluster;
/**
 * @author ACGG
 *
 */
public class Grouper {
    @SuppressWarnings("Duplicates")
    public static ArrayList<Degree> greedyGrouper_1(int k, ArrayList<Degree> degrees) {
        ArrayList<Degree> newDegrees = new ArrayList<>();

        // Genero il primo cluster
        int firstClusterDeg = degrees.get(0).getDegree();
        for (int i = 0; i < k; i++) {
            Degree toAdd = new Degree(degrees.get(i).getName(), firstClusterDeg);
            newDegrees.add(toAdd);
        }

        int i = k;
        while (i+k+1 < degrees.size()) { // while (newDegrees.size() != degrees.size()) {
            // Grado dell'elemento corrente
            int thisDegree = degrees.get(i).getDegree();
            // Grado dell'elemento i+k
            int lastDegree = degrees.get(i+k).getDegree();

            // Calcolo cMerge e cNew
            int endLimit1 = (i+2*k +1 < newDegrees.size()) ? i+2*k +1 : (newDegrees.size());
            int endLimit2 = (i+2*k-1 +1 < newDegrees.size()) ? i+2*k-1 +1 : (newDegrees.size());

            int cMerge  = (thisDegree - lastDegree) + computeI_1(degrees, i+k+1, endLimit1);
            int cNew    = computeI_1(degrees, i+k, endLimit2);

            if (cMerge > cNew) {
                // Creo un nuovo cluster
                for (int j = i; j < i+k; j++) {
                    Degree toAdd = new Degree(degrees.get(j).getName(), thisDegree);
                    newDegrees.add(toAdd);
                }
                i += k;
            } else {
                // Aggiungo il nodo al cluster corrente
                Degree toAdd = new Degree(degrees.get(i).getName(), newDegrees.get(i-1).getDegree());
                newDegrees.add(toAdd);
                i++;
            }
        }
        // Ciclo e aggiungo all'ultimo cluster
        int lastClusterDeg = newDegrees.get(i-1).getDegree();
        for (int j = i; j < degrees.size(); j++) {
            Degree toAdd = new Degree(degrees.get(j).getName(), lastClusterDeg);
            newDegrees.add(toAdd);
        }
        return newDegrees;
    }

    private static int computeI_1(ArrayList<Degree> degrees, int start, int end) {
        int startDegree = degrees.get(start).getDegree();
        int I = 0;
        for (int i = start; i < end; i++) {
            I += (startDegree - degrees.get(i).getDegree());
        }
        return I;
    }

	public static ArrayList<Degree> greedyGrouper(int k, ArrayList<Degree> dist) {
        ArrayList<Degree> newDist = new ArrayList<>();
        //Collections.sort(dist);

        int idx = 0;
        int C1 = dist.get(idx).getDegree();
        for (int i = idx; i < k; i++) {
            Degree degree = new Degree(dist.get(i).getName(), C1);
            newDist.add(degree);
        }

        idx = k;
        while (newDist.size() != dist.size()) {
            if ((idx + k) > dist.size()) {
                k = dist.size() - idx;
                for(int i = 0; i < k; i++, idx++) {
                    Degree degree = new Degree(dist.get(idx).getName(), newDist.get(idx - 1).getDegree());
                    newDist.add(degree);
                }
                break;
            }

            // Possibile errore nel calcolo di cMerge e cNew
            int Cmerge  = (dist.get(0).getDegree() - dist.get(idx).getDegree()) + computeI(idx + 1, idx + k - 1, dist);
            int Cnew    = computeI(idx, idx + k - 1, dist);

            if (Cmerge > Cnew) {
                //new cluster creation
                int ngc = dist.get(idx).getDegree();
                for(int i = idx; i<idx+k; i++) {
                    Degree degree = new Degree(dist.get(i).getName(), ngc);
                    newDist.add(degree);
                }
                idx += k;
            } else {
                //merge section
                Degree degree = new Degree(dist.get(idx).getName(), newDist.get(idx - 1).getDegree());
                newDist.add(degree);
                idx++;
            }
        }
        return newDist;
    }

    public static ArrayList<Degree> dpGrouper_OLD(int k, ArrayList<Degree> dist, ArrayList<DA> das) {
        ArrayList<Degree> newDist = new ArrayList<>();

        //Collections.sort(dist);

        int firstDegree = dist.get(0).getDegree();

        for (int i = 0; i < ((2 * k) - 1); i++) {
            ArrayList<ArrayList<Integer>> degrees = new ArrayList<>();

            ArrayList<Integer> deg = new ArrayList<>();
            for(int j = 0 ; j <= i; j++) {
                deg.add(firstDegree);
            }

            degrees.add(deg);

            DA da = new DA(degrees, computeI(0, i, dist));
            das.add(da);
        }

        for (int j = 2*k; j <= dist.size(); j++) {
            ArrayList<Cluster> tmp = new ArrayList<>();
            //int localId = 1;
            for (int t = k, localId = 1; t <= j-k; t++, localId++) {
                // Blocco inutile ??
//                ArrayList<Integer> d = new ArrayList<>();
//                for (int l = 0; l < t; l++) {
//                    d.add(firstDegree);
//                }
                ////////////

                Cluster onet = new Cluster(localId, das.get(t-1).getSeq(), das.get(t-1).getCost());

                ArrayList<ArrayList<Integer>> adt = new ArrayList<> ();
                ArrayList<Integer> dt = new ArrayList<>();
                for (int l = t; l < j; l++){
                    dt.add(dist.get(t).getDegree());
                }
                adt.add(dt);

                Cluster tponei = new Cluster(localId, adt, computeI(t, j-1, dist));

                int cNew = onet.getCost() + tponei.getCost();
                int cMerge = computeI(0, j-1, dist);

//				System.out.println("cmerge : " + cMerge + " cnew: " + cNew);

                // qui valuto se fare il merging sul cluster precedente o se creare un nuovo cluster
                if (cNew < cMerge) {
                    //System.out.println("NEW");
                    tmp.add(onet);
                    tmp.add(tponei);
                } else {
//					System.out.println("MERGE");
                    ArrayList<Integer> deg = new ArrayList<>();
                    for (int z = 0; z < j; z++) {
                        deg.add(firstDegree);
                    }
                    ArrayList<ArrayList<Integer>> adeg = new ArrayList<> ();
                    adt.add(deg);
                    Cluster oneinew = new Cluster(localId++, adeg, computeI(0, j, dist));
                    tmp.add(oneinew);
                }
                //localId++;
            }

//			for(Cluster cluster : tmp){
//				System.out.println(cluster.id + " " + cluster.degrees + " " + cluster.cost);
//			}

            Cluster c1 = null, c2 = null;
            //Cluster c2 = null;
            int v   = 0;
            int min = Integer.MAX_VALUE;
            while (v < tmp.size()){
                if (tmp.get(v).getCost() + tmp.get(v+1).getCost() <= min){
                    min = tmp.get(v).getCost() + tmp.get(v+1).getCost();
                    c1  = tmp.get(v);
                    c2  = tmp.get(v+1);
                }
                v += 2;
            }

            ArrayList<ArrayList<Integer>> agg = new ArrayList<> ();
            agg.addAll(c1.getDegrees());
            agg.addAll(c2.getDegrees());
            das.add(new DA(agg, (c1.getCost() + c2.getCost())));
        }

        //qui creo il newdist da DA(1, dist.size())
        int cnt = 0;
        ArrayList<ArrayList<Integer>> sol = das.get(das.size()-1).getSeq();
        for (ArrayList<Integer> arr : sol) {
            for (Integer i : arr) {
                Degree grado = new Degree(dist.get(cnt).getName(), i);
                newDist.add(grado);
                cnt++;
            }
        }
        return newDist;
    }

    private static int computeI_OLD(int i, int j, ArrayList<Degree> dist ) {
        int I = 0;
        for (int l = i; l <= j; l++) {
            I += (dist.get(i).getDegree() - dist.get(l).getDegree());
        }
        return I;
    }


    @SuppressWarnings("Duplicates")
    public static ArrayList<Degree> dpGrouper(Integer k, ArrayList<Degree> dist, ArrayList<DA> das){
        ArrayList<Degree> NewDist = new ArrayList<Degree>();

        Collections.sort(dist);
        /*Collections.sort(dist, new Comparator<Degree>(){
            @Override
            public int compare(Degree o1, Degree o2) {
                return o2.getDegree().compareTo(o1.getDegree());
            }
        });*/

//		for(Degree degree : dist){
//			System.out.println(degree.name + " " + degree.degree);
//		}

        int firstDegree = dist.get(0).getDegree();

        for(int i=0; i<2*k-1; i++){
            ArrayList<Integer> degree = new ArrayList<Integer>();
            for(int j = 0 ; j<=i; j++){
                degree.add(firstDegree);
            }


            ArrayList<ArrayList<Integer>> deg = new ArrayList<ArrayList<Integer>>();
            deg.add(degree);
            DA da = new DA(deg, computeI(0, i, dist));
            das.add(da);
        }

        for(int j = 2*k; j<=dist.size(); j++){
            ArrayList<Cluster> tmp = new ArrayList<Cluster>();
            int localId = 1;

            for(int t=k ; t<=j-k; t++)
            {
                //System.out.println("J: " +j + " T: " + t);
                int cNew = 0;
                int cMerge = 0;
                ArrayList<Integer> d = new ArrayList<Integer>();
                for(int l=0; l<t;l++){
                    d.add(firstDegree);
                }

                Cluster onet = new Cluster(localId, das.get(t-1).getSeq(), das.get(t-1).getCost());

                ArrayList<Integer> dt = new ArrayList<Integer>();
                for(int l=t; l<j;l++){
                    dt.add(dist.get(t).getDegree());
                }
                ArrayList<ArrayList<Integer>> adt = new ArrayList<ArrayList<Integer>> ();
                adt.add(dt);

                Cluster tponei = new Cluster(localId, adt, computeI(t, j-1, dist));

                cNew = onet.getCost() + tponei.getCost();

                cMerge = computeI(0, j-1, dist);

//				System.out.println("cmerge : " + cMerge + " cnew: " + cNew);

                // qui valuto se fare il merging sul cluster precedente o se creare un nuovo cluster
                if(cNew<cMerge){
                    //System.out.println("NEW");
                    tmp.add(onet);
                    tmp.add(tponei);
                }

                else {
//					System.out.println("MERGE");
                    ArrayList<Integer> deg = new ArrayList<Integer>();
                    for(int z = 0; z<j; z++){
                        deg.add(firstDegree);
                    }
                    ArrayList<ArrayList<Integer>> adeg = new ArrayList<ArrayList<Integer>> ();
                    adt.add(deg);
                    Cluster oneinew = new Cluster(localId++, adeg, computeI(0, j, dist));
                    tmp.add(oneinew);
                }
                localId++;
            }

//			for(Cluster cluster : tmp){
//				System.out.println(cluster.id + " " + cluster.degrees + " " + cluster.cost);
//			}

            Cluster c1 = null;
            Cluster c2 = null;
            int v = 0;
            int min = Integer.MAX_VALUE;
            while(v<tmp.size()){
                if(tmp.get(v).getCost() + tmp.get(v+1).getCost()<=min){
                    min = tmp.get(v).getCost() + tmp.get(v+1).getCost();
                    c1= tmp.get(v);
                    c2 = tmp.get(v+1);
                }
                v = v+2;
            }

            ArrayList<ArrayList<Integer>> agg = new ArrayList<ArrayList<Integer>> ();
            agg.addAll(c1.getDegrees());
            agg.addAll(c2.getDegrees());
            das.add(new DA(agg, (c1.getCost() + c2.getCost())));

//			System.out.println("DA: ");
//			for(DA da : das){
//				System.out.println(da.getSeq() + " " + da.getCost());
//			}
//			System.out.println("----------------------------------------");
        }

        //qui creo il newdist da DA(1, dist.size())
        int cnt = 0;
        ArrayList<ArrayList<Integer>> sol = das.get(das.size()-1).getSeq();
        for(ArrayList<Integer> arr : sol){
            for(Integer i: arr){
                Degree grado = new Degree(dist.get(cnt).getName(), i);
                NewDist.add(grado);
                cnt++;
            }
        }

        return NewDist;
    }

    private static int computeI(int i, int j, ArrayList<Degree> dist ) {
        int I = 0;
        for(int l = i; l<=j; l++)
        {
            I = I + dist.get(i).getDegree() - dist.get(l).getDegree();
        }
        return I;
    }

}
