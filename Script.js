
    async function handleTranslate() {
      const text   = document.getElementById('inputText').value.trim();
      const target = document.getElementById('targetLang').value;
      const btn    = document.getElementById('runBtn');
      const errDiv = document.getElementById('errorMsg');
      const outTxt = document.getElementById('outputText');
      const cpyBtn = document.getElementById('copyBtn');

      if (!text) { showError('no input text.'); return; }

      hideError();
      outTxt.style.display = 'none';
      cpyBtn.style.display = 'none';
      btn.disabled = true;
      btn.innerHTML = '<span class="dot-loader"><span></span><span></span><span></span></span>Running';

      try {
        const response = await fetch('https://api.sarvam.ai/translate', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'api-subscription-key': 'sk_omcvorv5_BpoR2qwjKsiyqZsp06bc7NxD'
  },
  body: JSON.stringify({
    input: text,
    source_language_code: 'auto',
    target_language_code: target,
    speaker_gender: 'Male',
    mode: 'formal',
    model: 'mayura:v1',
    enable_preprocessing: false
  })
});

if (!response.ok) throw new Error(`HTTP ${response.status}`);

const data = await response.json();
outTxt.textContent   = data.translated_text;  
outTxt.style.display = 'block';
cpyBtn.style.display = 'block';

      } catch (err) {
        showError('error: ' + err.message + '. is the server running?');
        console.error(err);
      } finally {
        btn.disabled    = false;
        btn.textContent = 'Translate';
      }
    }

    function handleCopy() {
      const text = document.getElementById('outputText').textContent;
      navigator.clipboard.writeText(text).then(() => {
        const btn = document.getElementById('copyBtn');
        btn.textContent = 'Copied';
        setTimeout(() => btn.textContent = 'Copy', 2000);
      });
    }

    function showError(msg) {
      const el = document.getElementById('errorMsg');
      el.textContent  = msg;
      el.style.display = 'block';
    }

    function hideError() {
      document.getElementById('errorMsg').style.display = 'none';
    }

    document.getElementById('inputText').addEventListener('keydown', function(e) {
      if (e.ctrlKey && e.key === 'Enter') handleTranslate();
    });
