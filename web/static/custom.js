document.getElementById('fileCheckBox').addEventListener('change', toggleContainers);
document.getElementById('urlCheckBox').addEventListener('change', toggleContainers);
document.getElementById('readOnlyModeCheckBox').addEventListener('change', togglePostInfo);

function findAncestorWithClass(el, cls) {
  while (el && !el.classList.contains(cls)) {
    el = el.parentElement
  }
  return el;
}

// Show information regarding POST requests if read-only mode is disabled.
function togglePostInfo(e) {
  var i;
  var checked = e.currentTarget.checked;
  var postInfos = document.querySelectorAll('.post');
  for (i = 0; i < postInfos.length; i++) {
    if (checked) {
      postInfos[i].classList.add('hidden');
    } else {
      postInfos[i].classList.remove('hidden');
    }
  }
}


function toggleContainers(e) {
  var checked = e.currentTarget.checked;
  var currentContainer = findAncestorWithClass(e.currentTarget, 'container');

  if (currentContainer) {
    var otherContainer;
    var containerId = currentContainer.id;
    if (containerId === 'file') {
      otherContainer = document.getElementById('url');
    } else {
      otherContainer = document.getElementById('file');
    }

    var otherCheckbox = otherContainer.querySelector('input[type="checkbox"]');
    var otherInput = otherContainer.querySelector('.fullWidth');
    var currentInput = currentContainer.querySelector('.fullWidth');
    otherContainer = otherContainer.querySelector('.content');
    currentContainer = currentContainer.querySelector('.content');

    if (checked) {
      currentContainer.classList.remove('disabled');
      otherContainer.classList.add('disabled');
      otherCheckbox.checked = false;

      otherInput.setAttribute('disabled', 'true');
      currentInput.removeAttribute('disabled');
    } else {
      currentContainer.classList.add('disabled');
      otherContainer.classList.remove('disabled');
      otherCheckbox.checked = true;

      currentInput.setAttribute('disabled', 'true');
      otherInput.removeAttribute('disabled');
    }
  }
}
