package com.thomaschampagne.hourglass.library.model;

public class Archive {

	protected Integer archiveFilesCount;
	protected Integer archiveFileSizeBytes;
	protected String archiveMd5FingerPrint;
	protected String archiveBinaryLink;
	protected Boolean archiveFromCache;
	
	public Integer getArchiveFilesCount() {
		return archiveFilesCount;
	}

	public void setArchiveFilesCount(Integer archiveFilesCount) {
		this.archiveFilesCount = archiveFilesCount;
	}

	public Integer getArchiveFileSizeBytes() {
		return archiveFileSizeBytes;
	}

	public void setArchiveFileSizeBytes(Integer archiveFileSizeBytes) {
		this.archiveFileSizeBytes = archiveFileSizeBytes;
	}

	public String getArchiveMd5FingerPrint() {
		return archiveMd5FingerPrint;
	}

	public void setArchiveMd5FingerPrint(String archiveMd5FingerPrint) {
		this.archiveMd5FingerPrint = archiveMd5FingerPrint;
	}

	public String getArchiveBinaryLink() {
		return archiveBinaryLink;
	}

	public void setArchiveBinaryLink(String archiveBinaryLink) {
		this.archiveBinaryLink = archiveBinaryLink;
	}

	public Boolean getArchiveFromCache() {
		return archiveFromCache;
	}

	public void setArchiveFromCache(Boolean archiveFromCache) {
		this.archiveFromCache = archiveFromCache;
	}

	@Override
	public String toString() {
		return "Archive [archiveFilesCount=" + archiveFilesCount
				+ ", archiveFileSizeBytes=" + archiveFileSizeBytes
				+ ", archiveMd5FingerPrint=" + archiveMd5FingerPrint
				+ ", archiveBinaryLink=" + archiveBinaryLink
				+ ", archiveFromCache=" + archiveFromCache + "]";
	}

}
