function getContainerVolumes(version){
	var oldContainers=[];
	if(version.containerDrafts!=null){
		oldContainers = version.containerDrafts;
		var hostVolumes = version.volumeDrafts;
		
		for(var i=0;i<oldContainers.length;i++){
			var volumeMountDrafts = oldContainers[i].volumeMountDrafts;
			if(volumeMountDrafts!=null && volumeMountDrafts.length>0){
				for(var j=0;j<volumeMountDrafts.length;j++){
					var volumeMount = volumeMountDrafts[j];
					volumeMount.volumeType="EMPTYDIR";
					if(hostVolumes!=null && hostVolumes.length>0){
						for(var m=0;m<hostVolumes.length;m++){
							var volume = hostVolumes[m];
							if(volumeMount.name == volume.name){
								volumeMount.volumeType=volume.volumeType;
								if(volume.hostPath!=null && volume.hostPath!=""){
									volumeMount.hostPath=volume.hostPath;
								}
								break;
							}
						}
					}
				}
			}
		}
	}
	return oldContainers;
}

function getVersion(containerMap){
	var version={};
	if(containerMap!=null){
		var containers = [];
		var volumeDrafts = [];
		for(var key in containerMap){
			var container = containerMap[key];
			console.log(container);

			var volumeMountDrafts =  container.volumeMountDrafts;
			if(volumeMountDrafts!=null && volumeMountDrafts.length>0){
				for(var j=0;j<volumeMountDrafts.length;j++){
					var volumeMount = volumeMountDrafts[j];
					var volumeDraft = {};
					volumeDraft.name=volumeMount.name;
					volumeDraft.volumeType=volumeMount.volumeType;
					if(volumeMount["volumeType"]=="HOSTPATH"){
						volumeDraft.hostPath=volumeMount.hostPath;
					}
					volumeDrafts.push(volumeDraft);
				}
			}
			
			containers.push(container);
		}
		
		version["containerDrafts"]=containers;
		version["volumeDrafts"]=volumeDrafts;
	}
	return version;
}