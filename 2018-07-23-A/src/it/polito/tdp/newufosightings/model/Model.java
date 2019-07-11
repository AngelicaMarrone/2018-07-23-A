package it.polito.tdp.newufosightings.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	//inserire tipo di dao

		private NewUfoSightingsDAO dao;

		private int anno;
		
		private State source;
		private State target;

		//scelta valore mappa

		private Map<String,State> idMap;

		

		//scelta tipo valori lista

		private List<State> vertex;

		

		//scelta tra uno dei due edges

		private List<Adiacenza> edges;

		

		//scelta tipo vertici e tipo archi

		private Graph<State, DefaultWeightedEdge> graph;


		

		public Model() {

			

			//inserire tipo dao

			dao  = new NewUfoSightingsDAO();

			//inserire tipo values

			idMap = new HashMap<String,State>();

		}

		

		public String creaGrafo(String forma) {

			

			//scelta tipo vertici e archi

			graph = new SimpleWeightedGraph<State,DefaultWeightedEdge>(DefaultWeightedEdge.class);

			

			//scelta tipo valori lista

			vertex = new ArrayList<State>(dao.loadAllStates(idMap));

			Graphs.addAllVertices(graph,vertex);

			

			edges = new ArrayList<Adiacenza>(dao.getEdges());

			

		for(Adiacenza a : edges) {

			source = idMap.get(a.getId1());

			target = idMap.get(a.getId2());

			double peso = dao.getPeso(anno,forma,a);
			
			DefaultWeightedEdge d = graph.getEdge(source, target);

			if(d==null)
			Graphs.addEdge(this.graph,source,target,a.getPeso());

			System.out.println("AGGIUNTO ARCO TRA: "+source.toString()+" e "+target.toString());

				

			}

			

		System.out.println("#vertici: "+graph.vertexSet().size());

		System.out.println("#archi: "+graph.edgeSet().size());

		String ris= calcolaVicini();
		
		return ris;
			

	}
	
	
	
	
	
	
	private String calcolaVicini() {
			String ris= "Ogni stato con relativo peso: \n";
			
			for(State s: this.graph.vertexSet())
			{ 	int peso=0;
				List<State> vicini= Graphs.neighborListOf(this.graph, s);
				for(State v: vicini)
				{
					DefaultWeightedEdge e= graph.getEdge(s, v);
					int temp= (int) graph.getEdgeWeight(e);
					peso+= temp;
					
				}
				
				ris+= s + " "+ peso + "\n";
			}
			
			return ris;
		}



	public List<String> getShapes(Integer anno) {
		
		List<String> shapes= dao.loadAllShapes(anno);
		this.anno=anno;
		
		return shapes;
	}

}
