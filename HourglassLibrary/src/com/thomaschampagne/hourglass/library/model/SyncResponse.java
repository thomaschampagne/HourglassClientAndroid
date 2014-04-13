package com.thomaschampagne.hourglass.library.model;

import java.util.List;

public class SyncResponse {

	protected Integer latestRevision;
	protected Integer latestRevisionDate;
	protected List<String> filesToDelete;
	protected Archive archive;

	public SyncResponse(Integer latestRevision, Integer latestRevisionDate,
			List<String> filesToDelete, Archive archive) {

		this.latestRevision = latestRevision;
		this.latestRevisionDate = latestRevisionDate;
		this.filesToDelete = filesToDelete;
		this.archive = archive;
	}

	public Integer getLatestRevision() {
		return latestRevision;
	}

	public void setLatestRevision(Integer latestRevision) {
		this.latestRevision = latestRevision;
	}

	public List<String> getFilesToDelete() {
		return filesToDelete;
	}

	public void setFilesToDelete(List<String> filesToDelete) {
		this.filesToDelete = filesToDelete;
	}

	public Archive getArchive() {
		return archive;
	}

	public void setArchive(Archive archive) {
		this.archive = archive;
	}

	public Integer getLatestRevisionDate() {
		return latestRevisionDate;
	}

	public void setLatestRevisionDate(Integer latestRevisionDate) {
		this.latestRevisionDate = latestRevisionDate;
	}

	@Override
	public String toString() {
		return "SyncResponse [latestRevision=" + latestRevision
				+ ", latestRevisionDate=" + latestRevisionDate
				+ ", filesToDelete=" + filesToDelete + ", archive=" + archive
				+ "]";
	}
}
