package webservice.demo.Controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import webservice.demo.Models.tools.Connect;
import webservice.demo.Models.information.Utilisateur;
import webservice.demo.Models.propriete.Parcelle;
import webservice.demo.Models.propriete.ParcelleCulture;
import webservice.demo.Models.propriete.Simulation;
import webservice.demo.Models.propriete.Terrain;
import webservice.demo.Models.information.Culture;
import webservice.demo.Models.information.Portefeuille;
import webservice.demo.Models.information.Saison;
import webservice.demo.Models.information.TypeCulture;
import webservice.demo.Models.discussion.Message;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.util.UUID;
import java.sql.Timestamp;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AppController {

    @GetMapping(value = "/accueilBack", produces = MediaType.APPLICATION_JSON_VALUE)
    public String accueilBack() {
        return "{\"message\": \"Welcome to the Back Office!\"}";
    }

    @GetMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public String login(@RequestParam String mail, @RequestParam String motDePasse)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Utilisateur u = new Utilisateur();
			int checkMail = u.checkMail(c,mail);
			if (checkMail > 0) {
				int checkLog = u.checkLogin(c,mail,motDePasse);
				if (checkLog > 0) {
					Utilisateur nU = u.findSpecifiedUtilisateur(c,mail,motDePasse);
					JSONObject utilisateurJson = new JSONObject();
				    utilisateurJson.put("id", nU.getId());
				    utilisateurJson.put("nom", nU.getNom());
				    utilisateurJson.put("prenom", nU.getPrenom());
				    utilisateurJson.put("dateNaissance", nU.getNaissance());
				    utilisateurJson.put("mail", nU.getMail());
				    utilisateurJson.put("motDePasse", nU.getMotDePasse());
				    utilisateurJson.put("pseudo", nU.getPseudo());
				    JSONArray jsonArray = new JSONArray();
				    jsonArray.put(utilisateurJson);
				    c.close();
				    return jsonArray.toString();
				}
				else{
					c.close();
					return "{ \"error\": \"Mot de Passe incorrect, veuillez réessayer\" }";
				}
			}
			else {
				c.close();
				return "{ \"error\": \"Vous n'avez pas de compte. Inscrivez-vous\" }";
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

    @PostMapping(value = "/insertUser", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertUser(@RequestParam String nom, @RequestParam String prenom,@RequestParam String naissance,@RequestParam String mail, @RequestParam String mdp, @RequestParam String pseudo)
    {
        try{
			Utilisateur u = new Utilisateur();
            Date nD = u.getSqlDate(naissance);
            Connect con = new Connect();
			Connection c = con.makeConnection();
			int checkMail = u.checkMail(c,mail);
			if (checkMail > 0) {
				c.close();
				return "{ \"error\": \"Ce mail est déja associé à un autre compte\" }";	
			}
            else {
				Utilisateur nU = new Utilisateur(nom,prenom,nD,mail,mdp,pseudo);
				u.insertUtilisateur(c,nU);
				c.close();
				return "{ \"success\": \"Insertion réussie\" }";	
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

    private String extractFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
    
        if (originalFileName != null) {
            String[] parts = originalFileName.split("\\.");
            if (parts.length > 1) {
                // Extract the file extension
                String extension = parts[parts.length - 1];
                return UUID.randomUUID().toString() + "." + extension;
            }
        }
    
        return UUID.randomUUID().toString();
    }
    
    private String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        String fileName = extractFileName(file);
        String sanitizedFileName = fileName.replaceAll("\\s+", "_");
        String fileExtension = sanitizedFileName.substring(sanitizedFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
    
        File uploadDirectory = new File(uploadPath);
        if (!uploadDirectory.exists()) {
            if (!uploadDirectory.mkdirs()) {
                throw new IOException("Failed to create upload directory");
            }
        }
    
        String filePath = uploadDirectory.getAbsolutePath() + File.separatorChar + uniqueFileName;
    
        try (InputStream input = file.getInputStream();
             OutputStream output = new FileOutputStream(filePath)) {
    
            byte[] buffer = new byte[1024];
            int length;
    
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new IOException("Failed to write the uploaded file", e);
        }
    
        return "user_uploads/" + uniqueFileName;
    }

    @PostMapping(value = "/insertCulture", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertCulture(@RequestParam String user, @RequestParam String nom, @RequestParam String type, @RequestParam String prixAchat,@RequestParam String prixVente, @RequestParam String saison, @RequestParam("photo") MultipartFile file,HttpServletRequest request) {
        String uploadPath = request.getServletContext().getRealPath("") + File.separator + "user_uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            Connect con = new Connect();
            Connection c = con.makeConnection();
            String pr = user;
            String n = nom;
            String t = type;
            double pA = Double.parseDouble(prixAchat);
            double pV = Double.parseDouble(prixVente);
            String s = saison;

            String filePath = uploadFile(file, uploadPath);

            Culture cul = new Culture();
            Culture nCul = new Culture(n, t, pA, pV, s, filePath);

            cul.insertCulture(c, nCul);

            String idCul = cul.getIdLastRecord(c);

            Culture nCul2 = new Culture(idCul, pr);

            cul.insertCultureUtilisateur(c, nCul2);

            c.close();
            return "{ \"success\": \"Insertion réussie\" }";
        } 

        catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"" + e.getMessage() + "\" }";
        }
    }

    @GetMapping(value = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTypes()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			TypeCulture t = new TypeCulture();
			TypeCulture[] allType = t.findType(c);
			JSONArray jsonArray = new JSONArray();
		    for (TypeCulture type : allType) {
		        JSONObject typeJson = new JSONObject();
		        typeJson.put("idType", type.getId());
		        typeJson.put("nom", type.getNom());
		        jsonArray.put(typeJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

    @GetMapping(value = "/saisons", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSaisons()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Saison s = new Saison();
			Saison[] allSaison = s.findSaison(c);
			JSONArray jsonArray = new JSONArray();
		    for (Saison saison : allSaison) {
		        JSONObject saisonJson = new JSONObject();
		        saisonJson.put("idSaison", saison.getId());
		        saisonJson.put("nom", saison.getNom());
		        saisonJson.put("debut", saison.getDebut());
		        saisonJson.put("fin", saison.getFin());
		        jsonArray.put(saisonJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

    @GetMapping(value = "/usercultures", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserCultures(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Culture t = new Culture();
			Culture[] allCulture = t.findSpecifiedUserCulture(c,nId);
			JSONArray jsonArray = new JSONArray();
		    for (Culture culture : allCulture) {
		        JSONObject cultureJson = new JSONObject();
		        cultureJson.put("proprietaire", culture.getProprietaire());
		        cultureJson.put("idCulture", culture.getId());
		        cultureJson.put("nom", culture.getNom());
		        cultureJson.put("type", culture.getType());
		        cultureJson.put("prixAchat", culture.getPrixAchat());
		        cultureJson.put("prixVente", culture.getPrixVente());
		        cultureJson.put("saison", culture.getSaison());
		        cultureJson.put("photo", culture.getPhoto());
		        jsonArray.put(cultureJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PutMapping(value = "/validerTerrain", produces = MediaType.APPLICATION_JSON_VALUE)
    public String validerTerrain(@RequestParam String terrain)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String t = terrain;
			Terrain nT = new Terrain();
			nT.validerTerrain(c,t);
			c.close();
			return "{ \"success\": \"Validation réussie\" }";
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/terrains", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTerrains()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Terrain t = new Terrain();
			Terrain[] allTerrain = t.findAllTerrain(c);
			JSONArray jsonArray = new JSONArray();
		    for (Terrain terrain : allTerrain) {
		        JSONObject terrainJson = new JSONObject();
		        terrainJson.put("idTerrain", terrain.getId());
		        terrainJson.put("proprietaire", terrain.getProprietaire());
		        terrainJson.put("description", terrain.getDescription());
		        terrainJson.put("localisation", terrain.getLocalisation());
		        terrainJson.put("photo", terrain.getPhoto());
		        terrainJson.put("creation", terrain.getCreation());
		        terrainJson.put("etat", terrain.getEtat());
		        jsonArray.put(terrainJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/userterrains", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserTerrains(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Terrain t = new Terrain();
			Terrain[] allTerrain = t.findAllUserTerrain(c,nId);
			JSONArray jsonArray = new JSONArray();
		    for (Terrain terrain : allTerrain) {
		        JSONObject terrainJson = new JSONObject();
		        terrainJson.put("idTerrain", terrain.getId());
		        terrainJson.put("proprietaire", terrain.getProprietaire());
		        terrainJson.put("description", terrain.getDescription());
		        terrainJson.put("localisation", terrain.getLocalisation());
		        terrainJson.put("photo", terrain.getPhoto());
		        terrainJson.put("creation", terrain.getCreation());
		        terrainJson.put("etat", terrain.getEtat());
		        jsonArray.put(terrainJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/userterrainsnonvalide", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserTerrainsNonValide(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Terrain t = new Terrain();
			Terrain[] allTerrain = t.findAllUserTerrainNonValide(c,nId);
			JSONArray jsonArray = new JSONArray();
		    for (Terrain terrain : allTerrain) {
		        JSONObject terrainJson = new JSONObject();
		        terrainJson.put("idTerrain", terrain.getId());
		        terrainJson.put("proprietaire", terrain.getProprietaire());
		        terrainJson.put("description", terrain.getDescription());
		        terrainJson.put("localisation", terrain.getLocalisation());
		        terrainJson.put("photo", terrain.getPhoto());
		        terrainJson.put("creation", terrain.getCreation());
		        terrainJson.put("etat", terrain.getEtat());
		        jsonArray.put(terrainJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/userfirstterrains", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserFirstTerrains(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Terrain t = new Terrain();
			Terrain[] allTerrain = t.findAllFirstUserTerrain(c,nId);
			JSONArray jsonArray = new JSONArray();
		    for (Terrain terrain : allTerrain) {
		        JSONObject terrainJson = new JSONObject();
		        terrainJson.put("idTerrain", terrain.getId());
		        terrainJson.put("proprietaire", terrain.getProprietaire());
		        terrainJson.put("description", terrain.getDescription());
		        terrainJson.put("localisation", terrain.getLocalisation());
		        terrainJson.put("photo", terrain.getPhoto());
		        terrainJson.put("creation", terrain.getCreation());
		        terrainJson.put("etat", terrain.getEtat());
		        jsonArray.put(terrainJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PostMapping(value = "/insertTerrain", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertTerrain(@RequestParam String user, @RequestParam String description, @RequestParam String localisation, @RequestParam("photo") MultipartFile file, @RequestParam String creation,HttpServletRequest request) {
        String uploadPath = request.getServletContext().getRealPath("") + File.separator + "user_uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            Connect con = new Connect();
            Connection c = con.makeConnection();
            String pr = user;
            String desc = description;
            String l = localisation;

            String filePath = uploadFile(file, uploadPath);

            Terrain t = new Terrain();

            Date cr = t.getSqlDate(creation);

            int etat = 0;

            Terrain nT = new Terrain(pr,desc,l,filePath,cr,etat);

            t.insertTerrain(c, nT);

            c.close();
            return "{ \"success\": \"Insertion réussie\" }";
        } 

        catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"" + e.getMessage() + "\" }";
        }
    }

	@PostMapping(value = "/insertParcelle", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertParcelle(@RequestParam String longueur, @RequestParam String largeur,@RequestParam String terrain)
    {
        try{
			double lo = Double.parseDouble(longueur);
            double larg = Double.parseDouble(largeur);
            String t = terrain;
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Parcelle p = new Parcelle();
			Parcelle nP = new Parcelle(lo,larg,t);
			p.insertParcelle(c,nP);
			c.close();
			return "{ \"success\": \"Insertion réussie\" }";
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/parcellesterrain", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getParcellesTerrain(@RequestParam String terrain)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = terrain;
			Parcelle p = new Parcelle();
			Parcelle[] allParcelle = p.findSpecifiedTerrainParcelle(c,nId);
			JSONArray jsonArray = new JSONArray();
		    for (Parcelle parcelle : allParcelle) {
		        JSONObject parcelleJson = new JSONObject();
		        parcelleJson.put("idParcelle", parcelle.getId());
		        parcelleJson.put("superficie", parcelle.getSuperficie());
		        parcelleJson.put("terrain", parcelle.getTerrain());
		        jsonArray.put(parcelleJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PostMapping(value = "/insertParcelleCulturePossible", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertParcelleCulturePossible(@RequestParam String parcelle, @RequestParam String type)
    {
        try{
			String t = parcelle;
            String nT = type;
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Parcelle p = new Parcelle();
			Parcelle nP = new Parcelle(t,nT);
			p.insertParcelleCulturePossible(c,nP);
			c.close();
			return "{ \"success\": \"Insertion réussie\" }";
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/parcelleculturespossibles", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getParcelleCulturePossible(@RequestParam String parcelle)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = parcelle;
			Parcelle p = new Parcelle();
			Parcelle[] allParcelle = p.findSpecifiedParcelleCulturePossible(c,nId);
			JSONArray jsonArray = new JSONArray();
		    for (Parcelle parcelles : allParcelle) {
		        JSONObject parcelleJson = new JSONObject();
		        parcelleJson.put("idParcelle", parcelles.getId());
		        parcelleJson.put("culture", parcelles.getCulture());
		        jsonArray.put(parcelleJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PutMapping(value = "/updateTerrain", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateTerrain(@RequestParam String terrain, @RequestParam String description, @RequestParam("photo") MultipartFile file, HttpServletRequest request) {
        String uploadPath = request.getServletContext().getRealPath("") + File.separator + "user_uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            Connect con = new Connect();
            Connection c = con.makeConnection();
            String t = terrain;
			String d = description;

            String filePath = uploadFile(file, uploadPath);

            Terrain t2 = new Terrain(t,d,filePath);

			Terrain nT = new Terrain();

			nT.updateTerrain(c,t2);
            c.close();
            return "{ \"success\": \"Mise à jour réussie\" }";
        } 

        catch (Exception e) {
            e.printStackTrace();
            return "{ \"error\": \"" + e.getMessage() + "\" }";
        }
    }

	@GetMapping(value = "/cultures", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getCultures()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Culture t = new Culture();
			Culture[] allCulture = t.findAllUserCulture(c);
			JSONArray jsonArray = new JSONArray();
		    for (Culture culture : allCulture) {
		        JSONObject cultureJson = new JSONObject();
		        cultureJson.put("proprietaire", culture.getProprietaire());
		        cultureJson.put("idCulture", culture.getId());
		        cultureJson.put("nom", culture.getNom());
		        cultureJson.put("type", culture.getType());
		        cultureJson.put("prixAchat", culture.getPrixAchat());
		        cultureJson.put("prixVente", culture.getPrixVente());
		        cultureJson.put("saison", culture.getSaison());
		        cultureJson.put("photo", culture.getPhoto());
		        jsonArray.put(cultureJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/nbcultures", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNbCultures(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Culture nC = new Culture();
			double n = nC.getCultureNb(c, nId);
			JSONObject nbJson = new JSONObject();
			nbJson.put("cultures", n);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(nbJson);
			c.close();
			return jsonArray.toString();
			
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/nbterrains", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNbTerrains(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Terrain nC = new Terrain();
			double n = nC.getTerrainNb(c, nId);
			JSONObject nbJson = new JSONObject();
			nbJson.put("terrains", n);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(nbJson);
			c.close();
			return jsonArray.toString();
			
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/nbparcellemoyen", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNbParcelleMoyen(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Parcelle nC = new Parcelle();
			double n = nC.getNbParcelleMoyen(c, nId);
			JSONObject nbJson = new JSONObject();
			nbJson.put("parcelles", n);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(nbJson);
			c.close();
			return jsonArray.toString();
			
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/surfacemoyenneparcelle", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSurfaceMoyenne(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Parcelle nC = new Parcelle();
			double n = nC.getSurfaceMoyenne(c, nId);
			JSONObject nbJson = new JSONObject();
			nbJson.put("surface", n);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(nbJson);
			c.close();
			return jsonArray.toString();
			
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/nbsimulation", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getNbSimulation(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Simulation nC = new Simulation();
			double n = nC.getNbSimulation(c, nId);
			JSONObject nbJson = new JSONObject();
			nbJson.put("simulation", n);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(nbJson);
			c.close();
			return jsonArray.toString();
			
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/rendement", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRendement(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Simulation nC = new Simulation();
			double n = nC.getQuantiteSimulation(c, nId);
			ParcelleCulture p = new ParcelleCulture();
			double nP = p.getQuantiteCulture(c,nId);
			double r = nC.getTauxRendement(nP,n);
			JSONObject nbJson = new JSONObject();
			nbJson.put("rendement", r);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(nbJson);
			c.close();
			return jsonArray.toString();
			
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PostMapping(value = "/insertParcelleCulture", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertParcelleCulture(@RequestParam String parcelle,@RequestParam String culture,@RequestParam String quantite,@RequestParam String date)
    {
        try{
			Utilisateur u = new Utilisateur();
            Date nD = u.getSqlDate(date);
			double q = Double.parseDouble(quantite);
			Connect con = new Connect();
			Connection c = con.makeConnection();
			ParcelleCulture p = new ParcelleCulture();
			ParcelleCulture nP = new ParcelleCulture(parcelle, culture, q, nD);
			p.insertParcelleCulture(c,nP);
			c.close();
			return "{ \"success\": \"Insertion réussie\" }";
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/cultures/type", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllCultures(@RequestParam String type)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Culture t = new Culture();
			Culture[] allCulture = t.findSpecifiedAllUserTypeCulture(c,type);
			JSONArray jsonArray = new JSONArray();
		    for (Culture culture : allCulture) {
		        JSONObject cultureJson = new JSONObject();
		        cultureJson.put("proprietaire", culture.getProprietaire());
		        cultureJson.put("idCulture", culture.getId());
		        cultureJson.put("nom", culture.getNom());
		        cultureJson.put("type", culture.getType());
		        cultureJson.put("prixAchat", culture.getPrixAchat());
		        cultureJson.put("prixVente", culture.getPrixVente());
		        cultureJson.put("saison", culture.getSaison());
		        jsonArray.put(cultureJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/cultures/proprietaire", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllCulturesProprietaire(@RequestParam String proprietaire)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Culture t = new Culture();
			Culture[] allCulture = t.findSpecifiedAllUserCulture(c,proprietaire);
			JSONArray jsonArray = new JSONArray();
		    for (Culture culture : allCulture) {
		        JSONObject cultureJson = new JSONObject();
		        cultureJson.put("proprietaire", culture.getProprietaire());
		        cultureJson.put("idCulture", culture.getId());
		        cultureJson.put("nom", culture.getNom());
		        cultureJson.put("type", culture.getType());
		        cultureJson.put("prixAchat", culture.getPrixAchat());
		        cultureJson.put("prixVente", culture.getPrixVente());
		        cultureJson.put("saison", culture.getSaison());
		        jsonArray.put(cultureJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/cultures/parcelle", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllCulturesParcelleHistorique(@RequestParam String parcelle)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			ParcelleCulture t = new ParcelleCulture();
			ParcelleCulture[] allCulture = t.findSpecifiedParcelleHistoriqueCulture(c,parcelle);
			JSONArray jsonArray = new JSONArray();
		    for (ParcelleCulture culture : allCulture) {
		        JSONObject cultureJson = new JSONObject();
				cultureJson.put("terrain", culture.getTerrain());
		        cultureJson.put("parcelle", culture.getId());
				cultureJson.put("culture", culture.getCulture());
				cultureJson.put("quantite", culture.getQuantite());
				cultureJson.put("cout", culture.getCoutRevient());
				cultureJson.put("date", culture.getDateCulture());
		        jsonArray.put(cultureJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PostMapping(value = "/insertSimulation", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertSimulation(@RequestParam String parcelle,@RequestParam String culture,@RequestParam String quantite,@RequestParam String date)
    {
        try{
			Utilisateur u = new Utilisateur();
            Date nD = u.getSqlDate(date);
			double q = Double.parseDouble(quantite);
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Simulation p = new Simulation();
			Simulation nP = new Simulation(parcelle, culture, q, nD);
			p.insertSimulation(c,nP);
			c.close();
			return "{ \"success\": \"Insertion réussie\" }";
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/simulations", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllUserSimulation(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Simulation t = new Simulation();
			Simulation[] allSimulation = t.findHistoriqueSimulationProprietaire(c,user);
			JSONArray jsonArray = new JSONArray();
		    for (Simulation simulation : allSimulation) {
		        JSONObject simulationJson = new JSONObject();
		        simulationJson.put("id", simulation.getId());
				simulationJson.put("parcelle", simulation.getParcelle());
				simulationJson.put("culture", simulation.getCulture());
				simulationJson.put("quantite", simulation.getQuantite());
				simulationJson.put("date", simulation.getSimulation());
				simulationJson.put("terrain", simulation.getTerrain());
				simulationJson.put("proprietaire", simulation.getProprietaire());
		        jsonArray.put(simulationJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/simulations/terrain", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllUserSimulation(@RequestParam String user,@RequestParam String terrain)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Simulation t = new Simulation();
			Simulation[] allSimulation = t.findHistoriqueSimulationProprietaireTerrain(c,user,terrain);
			JSONArray jsonArray = new JSONArray();
		    for (Simulation simulation : allSimulation) {
		        JSONObject simulationJson = new JSONObject();
		        simulationJson.put("id", simulation.getId());
				simulationJson.put("parcelle", simulation.getParcelle());
				simulationJson.put("culture", simulation.getCulture());
				simulationJson.put("quantite", simulation.getQuantite());
				simulationJson.put("date", simulation.getSimulation());
				simulationJson.put("terrain", simulation.getTerrain());
				simulationJson.put("proprietaire", simulation.getProprietaire());
		        jsonArray.put(simulationJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PostMapping(value = "/insertMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertMessage(@RequestParam String envoyeur, @RequestParam String receveur,@RequestParam String contenu)
    {
        try{
			Connect con = new Connect();
			MongoDatabase c = con.makeMongoConnection();
			Message m = new Message();
			Timestamp t = m.getNow();
			Message nM = new Message(envoyeur,receveur,contenu,t);
			m.insertMessage(c,nM);
			return "{ \"success\": \"Insertion réussie\" }";
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/getMessageEnvoye", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMessageEnvoye(@RequestParam String envoyeur, @RequestParam String receveur)
    {
        try{
			Connect con = new Connect();
			MongoDatabase c = con.makeMongoConnection();
			Message m = new Message();
			Message[] allMessage = m.findConservationUser(c,envoyeur,receveur);
			JSONArray jsonArray = new JSONArray();
		    for (Message message : allMessage) {
		        JSONObject messageJson = new JSONObject();
		        messageJson.put("envoyeur", message.getEnvoyeur());
				messageJson.put("receveur", message.getReceveur());
				messageJson.put("contenu", message.getContenu());
				messageJson.put("envoi", message.getEnvoi());
		        jsonArray.put(messageJson);
		    }
			return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/getMessageRecu", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getMessageRecu(@RequestParam String envoyeur, @RequestParam String receveur)
    {
        try{
			Connect con = new Connect();
			MongoDatabase c = con.makeMongoConnection();
			Message m = new Message();
			Message[] allMessage = m.findConservationUser(c,receveur,envoyeur);
			JSONArray jsonArray = new JSONArray();
		    for (Message message : allMessage) {
		        JSONObject messageJson = new JSONObject();
		        messageJson.put("envoyeur", message.getEnvoyeur());
				messageJson.put("receveur", message.getReceveur());
				messageJson.put("contenu", message.getContenu());
				messageJson.put("envoi", message.getEnvoi());
		        jsonArray.put(messageJson);
		    }
			return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/allcultures", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllCultures()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Culture t = new Culture();
			Culture[] allCulture = t.findAllUserCulture(c);
			JSONArray jsonArray = new JSONArray();
		    for (Culture culture : allCulture) {
		        JSONObject cultureJson = new JSONObject();
		        cultureJson.put("proprietaire", culture.getProprietaire());
		        cultureJson.put("idCulture", culture.getId());
		        cultureJson.put("nom", culture.getNom());
		        cultureJson.put("type", culture.getType());
		        cultureJson.put("prixAchat", culture.getPrixAchat());
		        cultureJson.put("prixVente", culture.getPrixVente());
		        cultureJson.put("saison", culture.getSaison());
		        jsonArray.put(cultureJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/proprietaires", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getProprietaires()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Utilisateur t = new Utilisateur();
			Utilisateur[] all = t.findProprietaire(c);
			JSONArray jsonArray = new JSONArray();
		    for (Utilisateur utilisateur : all) {
		        JSONObject utilisateurJson = new JSONObject();
		        utilisateurJson.put("pseudo", utilisateur.getPseudo());
		        jsonArray.put(utilisateurJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/parcelles", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getParcelles()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Parcelle p = new Parcelle();
			Parcelle[] all = p.findParcelle(c);
			JSONArray jsonArray = new JSONArray();
		    for (Parcelle parcelle : all) {
		        JSONObject parcelleJson = new JSONObject();
		        parcelleJson.put("idParcelle", parcelle.getId());
				parcelleJson.put("superficie", parcelle.getSuperficie());
				parcelleJson.put("terrain", parcelle.getTerrain());
		        jsonArray.put(parcelleJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/parcellescultures", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getParcellesCultures()
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			ParcelleCulture p = new ParcelleCulture();
			ParcelleCulture[] all = p.findParcelleCultures(c);
			JSONArray jsonArray = new JSONArray();
		    for (ParcelleCulture parcelle : all) {
		        JSONObject parcelleJson = new JSONObject();
		        parcelleJson.put("terrain", parcelle.getTerrain());
				parcelleJson.put("idParcelle", parcelle.getId());
				parcelleJson.put("culture", parcelle.getCulture());
				parcelleJson.put("type", parcelle.getType());
				parcelleJson.put("quantite", parcelle.getQuantite());
				parcelleJson.put("date", parcelle.getDateCulture());
				parcelleJson.put("proprietaire", parcelle.getProprietaire());
		        jsonArray.put(parcelleJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }



	@GetMapping(value = "/parcellesculturespossibles", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserParcellesCulturesPossible(@RequestParam String utilisateur, @RequestParam String parcelle)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			ParcelleCulture p = new ParcelleCulture();
			ParcelleCulture[] all = p.findSpecifiedCultureParcellePossible(c, utilisateur, parcelle);
			JSONArray jsonArray = new JSONArray();
		    for (ParcelleCulture parc : all) {
		        JSONObject parcelleJson = new JSONObject();
		        parcelleJson.put("idParcelle", parc.getId());
				parcelleJson.put("type", parc.getType());
				parcelleJson.put("culture", parc.getCulture());
				parcelleJson.put("proprietaire", parc.getProprietaire());
		        jsonArray.put(parcelleJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/parcellesuser", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserParcelles(@RequestParam String utilisateur)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Parcelle p = new Parcelle();
			Parcelle[] all = p.findSpecifiedUserParcelle(c, utilisateur);
			JSONArray jsonArray = new JSONArray();
		    for (Parcelle parc : all) {
		        JSONObject parcelleJson = new JSONObject();
		        parcelleJson.put("idParcelle", parc.getId());
				parcelleJson.put("superficie", parc.getSuperficie());
				parcelleJson.put("terrain", parc.getTerrain());
				parcelleJson.put("proprietaire", parc.getProprietaire());
		        jsonArray.put(parcelleJson);
		    }
		    c.close();
		    return jsonArray.toString();
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@PostMapping(value = "/insertUserParcelleCulture", produces = MediaType.APPLICATION_JSON_VALUE)
    public String insertUserParcelleCulture(@RequestParam String utilisateur,@RequestParam String parcelle,@RequestParam String culture,@RequestParam String quantite,@RequestParam String date)
    {
        try{
			Utilisateur u = new Utilisateur();
            Date nD = u.getSqlDate(date);
			double q = Double.parseDouble(quantite);
			Connect con = new Connect();
			Connection c = con.makeConnection();
			Portefeuille po = new Portefeuille(); 
			int checkPortefeuille = po.checkPortefeuille(c,utilisateur);
			if (checkPortefeuille <= 0) {
				Portefeuille nPo = new Portefeuille(utilisateur);
				po.insertPortefeuille(c, nPo);
				String id = po.getIdLastRecord(c);
				Portefeuille nPo2 = new Portefeuille(id, 0);
				po.insertPortefeuilleActivite(c, nPo2);
				ParcelleCulture p = new ParcelleCulture();
				ParcelleCulture nP = new ParcelleCulture(parcelle, culture, q, nD);
				p.insertParcelleCulture(c,nP);
				double pR = p.getPrixAchatCulture(c,culture);
				double act = (pR * q) * (-1);
				Portefeuille nPo3 = new Portefeuille(id, act);
				po.insertPortefeuilleActivite(c, nPo3);
				c.close();
				return "{ \"success\": \"Insertion réussie\" }";
			}
			else{
				String id = po.getUserPortefeuilleId(c, utilisateur);
				ParcelleCulture p = new ParcelleCulture();
				ParcelleCulture nP = new ParcelleCulture(parcelle, culture, q, nD);
				p.insertParcelleCulture(c,nP);
				double pR = p.getPrixAchatCulture(c,culture);
				double act = (pR * q) * (-1);
				Portefeuille nPo3 = new Portefeuille(id, act);
				po.insertPortefeuilleActivite(c, nPo3);
				c.close();
				return "{ \"success\": \"Insertion réussie\" }";
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }

	@GetMapping(value = "/portefeuille", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPortefeuille(@RequestParam String user)
    {
        try{
			Connect con = new Connect();
			Connection c = con.makeConnection();
			String nId = user;
			Portefeuille p = new Portefeuille();
			double pC = p.getUserPortefeuille(c, user);
			JSONObject nbJson = new JSONObject();
			nbJson.put("portefeuille", pC);
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(nbJson);
			c.close();
			return jsonArray.toString();
			
		}

		catch (Exception e) {
			e.printStackTrace();
			return "{ \"error\": \"Oups... Quelque chose s'est mal passé\" }";
		}
    }
	
}
