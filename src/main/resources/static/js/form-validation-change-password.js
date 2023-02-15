(() => {
  'use strict'

  document.getElementById('confirmPassword').addEventListener('input', event => {
    const changePasswordForm = document.getElementById('changePasswordForm')
    if (changePasswordForm.classList.contains('was-validated')) {
      let confirmPasswordVal = $('#confirmPassword').val();
      if (confirmPasswordVal === '') {
        $('#confirmPasswordErrorMsg').html('Please repeat new password.')
      } else {
        $('#confirmPasswordErrorMsg').html('Not the same as new password.')
      }
      if ($('#newPassword').val() !== confirmPasswordVal) {
        document.getElementById('confirmPassword').setCustomValidity("Invalid field.");
      } else {
        document.getElementById('confirmPassword').setCustomValidity("");
      }
    }
  });
})()