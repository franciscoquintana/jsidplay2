package ui.entities.collection.service;

import java.io.File;
import java.util.ArrayList;

import javax.persistence.EntityManager;

import ui.entities.collection.HVSCEntry;
import ui.entities.config.Configuration;

import libsidutils.STIL;
import libsidutils.STIL.Info;
import libsidutils.STIL.STILEntry;
import libsidutils.STIL.TuneEntry;

public class STILService {

	private EntityManager em;

	public STILService(EntityManager em) {
		this.em = em;
	};

	public void add(final Configuration config, final File tuneFile,
			File root, HVSCEntry hvscEntry) {
		final STILEntry stilEntry = STIL.getSTIL(root, tuneFile);
		if (stilEntry != null) {
			// get STIL Global Comment
			hvscEntry.setStilGlbComment(stilEntry.globalComment);
			// add tune infos
			addSTILInfo(hvscEntry, stilEntry.infos, tuneFile);
			// go through subsongs & add them as well
			for (final TuneEntry entry : stilEntry.subtunes) {
				addSTILInfo(hvscEntry, entry.infos, tuneFile);
			}
		}
	}

	private void addSTILInfo(HVSCEntry hvscEntry, ArrayList<Info> infos,
			File tuneFile) {
		for (Info info : infos) {
			ui.entities.collection.StilEntry stil = new ui.entities.collection.StilEntry();
			stil.setStilName(info.name);
			stil.setStilAuthor(info.author);
			stil.setStilTitle(info.title);
			stil.setStilArtist(info.artist);
			stil.setStilComment(info.comment);
			stil.setHvscEntry(hvscEntry);
			try {
				em.persist(stil);
			} catch (Throwable e) {
				System.err.println("Tune: " + tuneFile.getAbsolutePath());
				System.err.println(e.getMessage());
			}
			hvscEntry.getStil().add(stil);
		}
	}

	public void clear() {
		em.createQuery("DELETE from StilEntry").executeUpdate();
	}

}