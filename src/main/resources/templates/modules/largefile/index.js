/**
 * 使用 application/json 接口
 */
// function uploadFileByJson() {
//     const file = $("#file")[0].files[0];
//     const formData = {};
//     formData['fileName'] = file.name;
//     let data = JSON.stringify(formData);
//     console.log(data);
//     $.ajax({
//         url: PATH + '/file/fileByJson',
//         contentType: 'application/json',
//         method: 'post',
//         data: data,
//         success: function (data) {
//             uploadFile(data)
//         }
//     })
// }

/**
 * 使用 application/x-www-form-urlencoded 接口
 */
function uploadFileByForm() {
    const file = $("#file")[0].files[0];
    const formData = {};
    formData['fileName'] = file.name;

    $.ajax({
        url: PATH + '/file/fileByForm',
        contentType: 'application/x-www-form-urlencoded',
        method: 'post',
        data: formData,
        dataType:"json",
        success: function (response) {
            if (response.code === 201) {
                showAlert(response.message);
                return
            }
            uploadFile(response)
        }
    })
}

/**
 * 上传文件
 */
function uploadFile(result) {
    const file = $("#file")[0].files[0];
    let chunkSize = 1024 * 1024;
    let chunkCount = Math.ceil(file.size / chunkSize);
    let uploadFileVO = result.data;
    let uploadChunkNumbers = uploadFileVO.chunkNumberList;

    if (chunkCount === uploadChunkNumbers.length) {
        const formData = {};
        formData['fileName'] = file.name;
        formData['chunkCount'] = chunkCount;
        $.ajax({
            url: PATH + '/file/marge',
            contentType: 'application/x-www-form-urlencoded',
            method: 'post',
            data: formData,
            dataType:"json",
            success: function (response) {
                showAlert(response.message);
            }
        })
    } else {
        let fileReader = new FileReader();
        fileReader.onload = function (e) {
            let data = e.target.result;
            let blob = new Blob([data], {type: file.type});
            let start = 0;
            for (let i = 0; i < chunkCount; i++) {
                let end = Math.min(start + chunkSize, file.size);
                // 分块已经上传
                if (uploadChunkNumbers.includes(i)) {
                    start = end;
                    continue;
                }
                let chunkBlob = blob.slice(start, end);
                let formData = new FormData();
                formData.append('chunkCount', chunkCount)
                formData.append('file', chunkBlob, file.name + '_' + i);
                /**
                 * processData: false : 告诉jquery不要对form进行处理
                 * contentType: false : 指定为false才能形成正确的Content-Type
                 */
                $.ajax({
                    url: PATH + '/file/upload',
                    type: 'POST',
                    async: true,
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: function (response) {
                        console.log(response);
                        if (response.code === 201) {
                            showAlert(response.message);
                        }
                    }
                });
                start = end;
            }
        };
        fileReader.readAsArrayBuffer(file);
    }
}

function showAlert(message) {
    const modalBody = document.getElementById('messageModalBody');
    modalBody.textContent = message;
    const modal = new bootstrap.Modal(document.getElementById('messageModal'));
    modal.show();
}