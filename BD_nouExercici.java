/*
PASSOS PREVIS: LLegir el contingut del fitxer PassosASeguir.txt 

EXERCICI:
Heu d'implementar el mètode consulta. Aquest mètode ha d'obtenir per cada professor de la base de dades, el dni del professor, i el mòdul i número de l'últim despatx en què ha estat assignat. 

Cal tenir en compte que: 
- Si un professor té una assignació amb instantFi nul, això vol dir que aquesta és la seva assignació actual, i per tant l'última del professor. 
- En qualsevol altre cas la darrera assignació d'un professor és la que té un instantFi més gran. 
- Mai hi haurà dues assignacions d'un mateix professor en un mateix instant. 
- Si un professor no té cap assignació, s'haurà de posar "XXX" com a mòdul i "YYY" com a número de despatx. 

En cas que s'identifiqui una de les situacions següents, el mètode ha de llançar una excepció identificada amb el codi d'error que s'indica.
10: No hi ha cap professor
11: Error intern

Pel joc de proves públic el resultat que s'obtindrà és el següent:
111 Omega 128
222 Omega 118

En el fitxer adjunt trobareu: 
- Els passos a seguir: (PassosASeguir.txt)
- La descripció del conjunt del programa Practica: (ProgramaPractica.pdf) 
- Les classes i mètodes per obtenir els paràmetres d'entrada: (MetodesAuxiliars.txt) 
- Les classes i mètodes per retornar el resultat i llençar excepcions: (MetodesAuxiliars.txt) 
- El projecte Eclipse que cal estendre. 

*/



/*SOLUCIO*/


/* Imports de la classe */
import java.sql.*;
/* Capa de Control de Dades */
class CtrlDadesPublic extends CtrlDadesPrivat {
    public ConjuntTuples consulta(Connection c, Tuple params) throws BDException {
        ConjuntTuples ct = new ConjuntTuples();
        try {
        	Statement st = c.createStatement();
        	ResultSet rs = st.executeQuery("select dni from professors");
        	PreparedStatement elsmaxs = c.prepareStatement("select  modul, numero from assignacions a, professors p where a.dni=p.dni and p.dni=? and instantfi=(select max(instantfi) from assignacions where dni=p.dni)");
        	PreparedStatement elsnulls = c.prepareStatement("select  modul, numero from assignacions a, professors p where a.dni=p.dni and p.dni=? and instantfi is null");
        	boolean noprofes = true;
        	/*Statement quen no necessitis parametres que canviin, Prepared statement quan si*/
        	while(rs.next()) {
        		noprofes=false;
        		String dni = rs.getString(1);
        		elsnulls.setString(1, dni);
        		ResultSet rsnulls = elsnulls.executeQuery();
        		String m = "XXX";
        		String n = "YYY";
        		Tuple t = new Tuple();
        		if(rsnulls.next()) {
        			m = rsnulls.getString(1);
        			n = rsnulls.getString(2);
        		} else {
        			elsmaxs.setString(1, dni);
            		ResultSet rsmaxs = elsmaxs.executeQuery();
            		if(rsmaxs.next()) {
            			m = rsmaxs.getString(1);
            			n = rsmaxs.getString(2);
            		}
        		}
        		t.afegir(dni);
        		t.afegir(m);
        		t.afegir(n);
        		ct.afegir(t);
        		
        	}
        	if(noprofes) throw new BDException(10);
        	st.close(); /*alliberas els recursos dels statements*/
        	elsmaxs.close();
        	elsnulls.close();
            return ct;
        } catch(SQLException sqle) {
            /*sqle.printStackTrace(); per saber que provoca lerror*/
            throw new BDException(11);
        }
        
    }
}
